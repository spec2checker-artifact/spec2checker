import yaml
import re
from typing import Dict, List, Any, Tuple, Optional
from LLM_to_IR.ir_model import RuleIntentIR

class SemgrepRuleGenerator:
    LANGUAGE_MAP = {
        "shell": "bash", "bash": "bash",
        "python": "python", "python3": "python", "py": "python",
        "java": "java",
        "javascript": "javascript", "js": "javascript",
        "typescript": "typescript", "ts": "typescript",
        "go": "go", "golang": "go",
        "cpp": "cpp", "c": "c",
        "ruby": "ruby", "php": "php",
        "csharp": "csharp", "c#": "csharp",
        "rust": "rust", "kotlin": "kotlin", "swift": "swift",
    }

    @staticmethod
    def _normalize_language(language: str) -> str:
        return SemgrepRuleGenerator.LANGUAGE_MAP.get(language.lower(), language.lower())

    def generate(self, ir: RuleIntentIR) -> Optional[str]:
        severity = ir.severity.upper()
        semgrep_hint = ir.backend_hints.get("semgrep")
        if semgrep_hint and not semgrep_hint.partial_support:
            return None

        # 规则可行性预检（新增对导入风格规则的屏蔽）
        if not self._is_rule_feasible(ir):
            return self._generate_disabled_rule(ir, "Rule requires capabilities beyond Semgrep (type resolution, scope analysis, complex syntax, or unreliable import organization)")

        use_taint = (
            ir.language.lower() == "java"
            and ir.dataflow_spec is not None
            and ir.capabilities_required.dataflow
            and (semgrep_hint.partial_support if semgrep_hint else True)
        )
        if use_taint:
            return self._generate_taint_rule(ir, severity)
        else:
            return self._generate_basic_rule(ir, severity)

    def _is_rule_feasible(self, ir: RuleIntentIR) -> bool:
        desc = ir.description.lower()
        # 屏蔽所有与 import 语句格式/排序/分组相关的规则（极易误报）
        if 'import' in desc and ('ordering' in desc or 'group' in desc or 'single group' in desc or 'non-static' in desc or 'static import' in desc):
            return False
        if ir.rule_id and 'import' in ir.rule_id and ('order' in ir.rule_id or 'group' in ir.rule_id):
            return False

        # 作用域约束：Semgrep 无法区分
        if ir.context_constraints:
            for c in ir.context_constraints:
                if c.constraint_type == "scope":
                    return False
                if c.constraint_type == "inheritance":
                    condition = c.condition or ""
                    if not re.match(r'^[A-Z][a-zA-Z0-9_]*(\.[a-zA-Z_][a-zA-Z0-9_]*)*$', condition):
                        return False
                if c.constraint_type == "syntactic":
                    if "name_matches_regex" in (c.condition or "") and c.required == "false":
                        return False
        # 需要类型解析
        semgrep_hint = ir.backend_hints.get("semgrep")
        if ir.capabilities_required.type_resolution and (not semgrep_hint or semgrep_hint.needs_type_solver):
            return False
        return True

    def _generate_disabled_rule(self, ir: RuleIntentIR, reason: str) -> Optional[str]:
        rule = {
            "id": ir.rule_id,
            "message": f"[DISABLED] {ir.description}",
            "languages": [self._normalize_language(ir.language)],
            "severity": ir.severity.upper(),
            "pattern": "__DISABLED_NO_PATTERN__",
            "metadata": {
                "disabled": True,
                "reason": reason,
                "original_description": ir.description,
            }
        }
        return yaml.dump({"rules": [rule]}, sort_keys=False, indent=2, allow_unicode=True)

    def _generate_basic_rule(self, ir: RuleIntentIR, severity: str) -> Optional[str]:
        rule = {
            "id": ir.rule_id,
            "message": ir.description,
            "languages": [self._normalize_language(ir.language)],
            "severity": severity,
        }
        patterns = []
        metadata = {}

        paths = self._get_file_paths(ir)
        if paths:
            rule["paths"] = paths

        main_pattern = self._prepare_main_pattern(ir)
        if main_pattern is None:
            return None
        if isinstance(main_pattern, list):
            rule["patterns"] = [{"pattern-either": main_pattern}]
        elif main_pattern:
            patterns.append({"pattern": main_pattern})
        else:
            return None

        if ir.surface_pattern.metavariables:
            for var, spec in ir.surface_pattern.metavariables.items():
                if spec.regex:
                    patterns.append({
                        "metavariable-regex": {"metavariable": var, "regex": spec.regex}
                    })
                elif spec.type_hint:
                    lang = self._normalize_language(ir.language).lower()
                    if lang != "python":
                        semgrep_type = self._map_type_hint(spec.type_hint)
                        patterns.append({
                            "metavariable-type": {"metavariable": var, "type": semgrep_type}
                        })

        if ir.context_constraints:
            constraint_patterns, constraint_metadata = self._process_context_constraints(
                ir.context_constraints, ir.language.lower()
            )
            patterns.extend(constraint_patterns)
            metadata.update(constraint_metadata)

        if "patterns" not in rule:
            if len(patterns) == 1:
                rule["pattern"] = patterns[0]["pattern"]
            elif len(patterns) > 1:
                rule["patterns"] = patterns
            else:
                return None

        if "patterns" in rule:
            extra_exemptions = self._get_extra_exemptions(ir.rule_id, ir.language.lower())
            for pattern_not in extra_exemptions:
                rule["patterns"].append({"pattern-not": pattern_not})

        metadata["capabilities_required"] = self._serialize(ir.capabilities_required)
        metadata["backend_hints"] = self._serialize_backend_hints(ir.backend_hints)
        metadata["original_description"] = ir.description
        rule["metadata"] = metadata
        return yaml.dump({"rules": [rule]}, sort_keys=False, indent=2, allow_unicode=True)

    def _get_file_paths(self, ir: RuleIntentIR) -> Optional[Dict]:
        lang = self._normalize_language(ir.language).lower()
        desc = ir.description.lower()
        if lang in ("cpp", "c") and ("include" in desc and "guard" in desc):
            return {"include": ["*.h", "*.hpp"]}
        if lang == "python" and ("whitespace" in desc or "indentation" in desc):
            return {"include": ["*.py"]}
        if lang == "java" and ("whitespace" in desc or "format" in desc):
            return {"include": ["*.java"]}
        if lang == "bash" and ("format" in desc or "indent" in desc):
            return {"include": ["*.sh", "*.bash"]}
        return None

    def _prepare_main_pattern(self, ir: RuleIntentIR) -> Optional[Any]:
        lang = self._normalize_language(ir.language).lower()
        snippet = ir.surface_pattern.code_snippet.strip() if ir.surface_pattern.code_snippet else ""

        if not snippet or snippet in ('//', '# placeholder', 'TODO'):
            return self._create_fallback_pattern(ir)

        if snippet.startswith('/') and snippet.endswith('/'):
            snippet = snippet[1:-1].strip()

        # 如果 snippet 是过于宽泛的 import 模式，直接禁用
        if lang == 'java' and snippet.strip().startswith('import') and 'ordering' in ir.description.lower():
            return None

        if self._has_alternatives(snippet, lang):
            return self._extract_alternatives(snippet, lang)
        return snippet

    def _create_fallback_pattern(self, ir: RuleIntentIR) -> Optional[str]:
        desc = ir.description.lower()
        lang = self._normalize_language(ir.language).lower()

        # 对于复杂语义或不可靠的导入规则，不生成宽泛模式
        if lang in ('python',):
            if 'import' in desc and ('rename' in desc or 'packages' in desc or 'modules' in desc):
                return None
            if 'global state' in desc:
                return None
            if 'classmethod' in desc:
                return None
            if 'power feature' in desc:
                return None
            if 'none check' in desc:
                return None
        if lang == 'java':
            if 'import' in desc and ('ordering' in desc or 'group' in desc or 'static import' in desc):
                return None
        if lang in ('cpp', 'c'):
            if 'using namespace' in desc:
                return 'using namespace std;'
            if 'sizeof(' in desc:
                return 'sizeof($TYPE)'
            if 'auto ' in desc:
                return 'auto $VAR = $EXPR;'
            if 'override' in desc or 'virtual' in desc:
                return 'virtual $RET $METHOD(...) $SPECIFIER;'
        elif lang == 'java':
            if 'package' in desc:
                return 'package $PKG;'
            if 'class' in desc and 'camel' in desc:
                return 'class $CLASS { ... }'
        elif lang in ('javascript', 'typescript'):
            if 'goog.module' in desc:
                return 'goog.module.declareLegacyNamespace();'
            if 'import' in desc and 'extension' in desc:
                return 'import $IMPORT from "$PATH.js";'
        elif lang == 'bash':
            if 'backtick' in desc or 'dollar paren' in desc:
                return '`$CMD`'
            if 'quote' in desc and 'variable' in desc:
                return '$VAR'
            if 'control flow' in desc and 'then' in desc:
                return 'if [[ $COND ]];\nthen\n    ...\nfi'

        return ""

    def _has_alternatives(self, snippet: str, lang: str) -> bool:
        if lang == "bash":
            return "|" in snippet and ("(" in snippet or ")" in snippet)
        return False

    def _extract_alternatives(self, snippet: str, lang: str) -> Optional[List[Dict[str, str]]]:
        if lang == "bash":
            parts = re.split(r'\(\s*(.*?)\s*\)', snippet)
            if len(parts) == 3:
                prefix = parts[0].strip()
                alternatives = parts[1].strip()
                suffix = parts[2].strip()
                alts = [alt.strip() for alt in alternatives.split('|')]
                return [{"pattern": f"{prefix} {alt} {suffix}".strip()} for alt in alts]
        return None

    def _generate_taint_rule(self, ir: RuleIntentIR, severity: str) -> Optional[str]:
        rule = {
            "id": ir.rule_id,
            "message": ir.description,
            "languages": [self._normalize_language(ir.language)],
            "severity": severity,
            "mode": "taint",
        }
        if ir.dataflow_spec:
            if ir.dataflow_spec.source:
                rule["pattern-sources"] = [self._parse_dataflow_element(ir.dataflow_spec.source)]
            if ir.dataflow_spec.sink:
                rule["pattern-sinks"] = [self._parse_dataflow_element(ir.dataflow_spec.sink)]
            if ir.dataflow_spec.sanitizer:
                rule["pattern-sanitizers"] = [self._parse_dataflow_element(ir.dataflow_spec.sanitizer)]
        if "pattern-sources" not in rule or "pattern-sinks" not in rule:
            return self._generate_basic_rule(ir, severity)
        metadata = {
            "capabilities_required": self._serialize(ir.capabilities_required),
            "backend_hints": self._serialize_backend_hints(ir.backend_hints),
            "original_description": ir.description,
        }
        if ir.dataflow_spec:
            metadata["dataflow_spec"] = self._serialize(ir.dataflow_spec)
        rule["metadata"] = metadata
        return yaml.dump({"rules": [rule]}, sort_keys=False, indent=2, allow_unicode=True)

    def _map_type_hint(self, type_hint: str) -> str:
        mapping = {"function": "function", "class": "type", "expr": "expr"}
        return mapping.get(type_hint, "expr")

    def _process_context_constraints(self, constraints: list, language: str) -> Tuple[List[Dict], Dict]:
        patterns = []
        metadata = {}
        for c in constraints:
            if c.constraint_type == "annotation" and c.required:
                patterns.append({"pattern-inside": f"@{c.required}\n..."})
            elif c.constraint_type == "type" and c.condition:
                type_ps, type_md = self._process_type_constraint(c, language)
                patterns.extend(type_ps)
                metadata.update(type_md)
            elif c.constraint_type == "inheritance" and c.condition:
                inh_ps, inh_md = self._process_inheritance_constraint(c, language)
                patterns.extend(inh_ps)
                metadata.update(inh_md)
            elif c.constraint_type == "value" and c.condition:
                val_ps, val_md = self._process_value_constraint(c)
                patterns.extend(val_ps)
                metadata.update(val_md)
            elif c.constraint_type == "syntactic":
                syn_ps, syn_md = self._process_syntactic_constraint(c, language)
                patterns.extend(syn_ps)
                metadata.update(syn_md)
            else:
                metadata[f"{c.constraint_type}_constraint"] = c.condition or c.required
        return patterns, metadata

    def _process_syntactic_constraint(self, constraint, language: str) -> Tuple[List[Dict], Dict]:
        patterns = []
        metadata = {}
        cond = constraint.condition or ""
        target = constraint.target

        if "not_in_list" in cond:
            lst_match = re.search(r"not_in_list\((.+)\)", cond)
            if lst_match:
                items_str = lst_match.group(1)
                items = [i.strip().strip("'\"") for i in items_str.split(",")]
                for mod in items:
                    patterns.append({"pattern-not": f"from {mod} import $ITEM"})
                return patterns, metadata

        if "name_does_not_start_with" in cond:
            char = re.search(r"name_does_not_start_with\('([^']+)'\)", cond)
            if char:
                # Semgrep 无法简单排除以下划线开头的变量，故返回占位（可行性检查会拦截）
                metadata[f"syntax_{target}"] = cond
        # 其他语法约束放入 metadata
        metadata[f"syntax_{target}"] = cond
        return patterns, metadata

    def _process_type_constraint(self, constraint, language: str) -> Tuple[List[Dict], Dict]:
        patterns = []
        metadata = {}
        if language == "java" and re.match(r'^[a-zA-Z_][a-zA-Z0-9_]*(\.[a-zA-Z_][a-zA-Z0-9_]*)*$', constraint.condition):
            patterns.append({"metavariable-type": {"metavariable": constraint.target, "type": constraint.condition}})
        else:
            metadata[f"type_constraint_{constraint.target}"] = constraint.condition
        return patterns, metadata

    def _process_inheritance_constraint(self, constraint, language: str) -> Tuple[List[Dict], Dict]:
        patterns = []
        metadata = {}
        condition = constraint.condition or ""
        if "_" in condition or " " in condition:
            metadata[f"inheritance_constraint_{constraint.target}"] = condition
            return patterns, metadata
        if language == "java":
            patterns.append({"pattern-inside": f"class $C extends {condition} {{...}}"})
        elif language == "python":
            patterns.append({"pattern-inside": f"class $C({condition}): ..."})
        else:
            patterns.append({"pattern-inside": f"class $C {condition} {{...}}"})
        return patterns, metadata

    def _process_value_constraint(self, constraint) -> Tuple[List[Dict], Dict]:
        metadata = {}
        metadata[f"value_constraint_{constraint.target}"] = constraint.condition
        return [], metadata

    def _parse_dataflow_element(self, element: str) -> dict:
        element = element.strip()
        if element.startswith("$"):
            return {"metavariable": element}
        if "()" in element:
            func = element.replace("()", "")
            return {"pattern": f"{func}(...)"}
        return {"pattern": element}

    def _get_extra_exemptions(self, rule_id: str, language: str) -> List[str]:
        if language == "python" and rule_id == "python-imports-only-packages-modules":
            return [
                "from __future__ import $ITEM",
                "from absl import $ITEM",
                "from absl.testing import $ITEM",
                "from absl.flags import $ITEM",
                "from google.protobuf import $ITEM",
                "from src.test.py.bazel import $ITEM",
                "from unittest import $ITEM",
                "from urllib import $ITEM",
                "from xml.sax import $ITEM",
                "from abc import $ITEM",
                "from http import $ITEM",
                "from collections import $ITEM",
                "from src.main.protobuf import $ITEM",
            ]
        return []

    def _serialize(self, obj):
        if hasattr(obj, 'model_dump'):
            return obj.model_dump()
        elif hasattr(obj, '__dict__'):
            return obj.__dict__
        return obj

    def _serialize_backend_hints(self, backend_hints: dict) -> dict:
        result = {}
        for k, v in backend_hints.items():
            serialized = self._serialize(v)
            if isinstance(serialized, dict):
                result[k] = {sk: sv for sk, sv in serialized.items() if sv is not None}
            elif serialized is not None:
                result[k] = serialized
        return result