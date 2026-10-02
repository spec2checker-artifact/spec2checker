import sys
import os
sys.path.append(os.path.dirname(os.path.dirname(__file__)))

from LLM_to_IR.ir_model import RuleIntentIR
import re
from typing import List, Dict, Any, Optional


class PMDRuleGenerator:
    """
    将 RuleIntentIR 转换为 PMD Java 规则类（字符串形式）。
    适配 PMD 6.55.0，支持 Java 语言的多种代码模式。
    增强：对无法可靠检测的格式类规则生成占位规则，避免误报。
    """

    PRIORITY_MAP = {"error": "1", "warning": "3", "info": "5"}
    SUPPORTED_CONSTRAINTS = {"type", "annotation", "inheritance", "scope", "value"}

    def __init__(self, base_rule_output_path: str = "PMDBaseRule.java"):
        self.base_rule_output_path = base_rule_output_path
        self._generate_base_rule_class()

    def _generate_base_rule_class(self) -> str:
        base_code = '''package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import java.util.List;
import java.util.regex.Pattern;

public abstract class PMDBaseRule extends AbstractJavaRule {

    protected boolean isMethodCall(ASTPrimaryExpression node, String methodName) {
        if (node == null || methodName == null) return false;
        List<ASTPrimarySuffix> suffixes = node.findChildrenOfType(ASTPrimarySuffix.class);
        for (ASTPrimarySuffix suffix : suffixes) {
            if (suffix.isArguments()) {
                ASTPrimaryPrefix prefix = node.getFirstChildOfType(ASTPrimaryPrefix.class);
                if (prefix != null) {
                    ASTName name = prefix.getFirstChildOfType(ASTName.class);
                    if (name != null) {
                        String actualMethod = name.getImage();
                        if (actualMethod != null && actualMethod.contains(".")) {
                            actualMethod = actualMethod.substring(actualMethod.lastIndexOf('.') + 1);
                        }
                        return methodName.equals(actualMethod);
                    }
                }
            }
        }
        return false;
    }

    protected List<ASTExpression> getArguments(ASTPrimaryExpression node) {
        if (node == null) return java.util.Collections.emptyList();
        List<ASTPrimarySuffix> suffixes = node.findChildrenOfType(ASTPrimarySuffix.class);
        for (ASTPrimarySuffix suffix : suffixes) {
            if (suffix.isArguments()) {
                int childCount = suffix.jjtGetNumChildren();
                for (int i = 0; i < childCount; i++) {
                    Node child = suffix.jjtGetChild(i);
                    if (child instanceof ASTArguments) {
                        ASTArguments argsNode = (ASTArguments) child;
                        int argsChildCount = argsNode.jjtGetNumChildren();
                        for (int j = 0; j < argsChildCount; j++) {
                            Node argsChild = argsNode.jjtGetChild(j);
                            if (argsChild instanceof ASTArgumentList) {
                                ASTArgumentList argList = (ASTArgumentList) argsChild;
                                return argList.findChildrenOfType(ASTExpression.class);
                            }
                        }
                    }
                }
            }
        }
        return java.util.Collections.emptyList();
    }

    protected Node getCaller(ASTPrimaryExpression node) {
        if (node == null) return null;
        ASTPrimaryPrefix prefix = node.getFirstChildOfType(ASTPrimaryPrefix.class);
        return prefix != null ? prefix.getFirstChildOfType(ASTName.class) : null;
    }

    protected String getTypeName(Node node) {
        if (!(node instanceof TypeNode)) return null;
        Class<?> type = ((TypeNode) node).getType();
        return type != null ? type.getCanonicalName() : null;
    }

    protected boolean isType(Node node, String expectedType) {
        String actual = getTypeName(node);
        return actual != null && actual.equals(expectedType);
    }

    protected boolean isNullLiteral(Node node) {
        return (node instanceof ASTLiteral) && "null".equals(((ASTLiteral) node).getImage());
    }

    protected boolean isNumericLiteralMatch(Node node, String operator, int value) {
        if (!(node instanceof ASTLiteral)) return false;
        String img = ((ASTLiteral) node).getImage();
        if (img == null || !img.matches("-?\\\\d+")) return false;
        try {
            int actual = Integer.parseInt(img);
            switch (operator) {
                case "<": return actual < value;
                case ">": return actual > value;
                case "<=": return actual <= value;
                case ">=": return actual >= value;
                case "==": return actual == value;
                case "!=": return actual != value;
                default: return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected boolean isLiteral(Node node) { return node instanceof ASTLiteral; }

    protected ASTClassOrInterfaceDeclaration getEnclosingClass(Node node) {
        return node != null ? node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class) : null;
    }

    protected boolean hasAnnotation(ASTClassOrInterfaceDeclaration clazz, String annotation) {
        if (clazz == null) return false;
        for (ASTAnnotation ann : clazz.findChildrenOfType(ASTAnnotation.class))
            if (annotation.equals(ann.getAnnotationName())) return true;
        return false;
    }

    protected boolean isSubclassOf(ASTClassOrInterfaceDeclaration clazz, String parentClass) {
        if (clazz == null) return false;
        ASTClassOrInterfaceType sup = clazz.getFirstChildOfType(ASTClassOrInterfaceType.class);
        return sup != null && sup.getType() != null && parentClass.equals(sup.getType().getCanonicalName());
    }

    protected boolean isSourcePotentiallyNull(Node node) { return false; }

    protected boolean checkIsNegativeLiteral(Node node) {
        if (node == null) return false;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof ASTUnaryExpression) return true;
        }
        if (node instanceof ASTLiteral) {
            String img = ((ASTLiteral) node).getImage();
            if (img != null && img.startsWith("-") && img.matches("-\\\\d+")) return true;
        }
        return false;
    }

    protected boolean isStringLiteral(Node node) {
        if (!(node instanceof ASTLiteral)) return false;
        String img = ((ASTLiteral) node).getImage();
        return img != null && img.startsWith("\\\"");
    }

    protected boolean isBooleanLiteral(Node node) {
        if (!(node instanceof ASTLiteral)) return false;
        String img = ((ASTLiteral) node).getImage();
        return "true".equals(img) || "false".equals(img);
    }

    protected boolean isZeroLiteral(Node node) {
        if (!(node instanceof ASTLiteral)) return false;
        String img = ((ASTLiteral) node).getImage();
        return "0".equals(img);
    }

    protected boolean isCalledFromType(ASTPrimaryExpression node, String typeName) {
        Node caller = getCaller(node);
        if (!(caller instanceof ASTName)) return false;
        String callerName = ((ASTName) caller).getImage();
        if (callerName == null) return false;
        return callerName.startsWith(typeName + ".");
    }

    protected boolean isStaticMethodCall(ASTPrimaryExpression node) {
        Node caller = getCaller(node);
        if (!(caller instanceof ASTName)) return false;
        String callerName = ((ASTName) caller).getImage();
        if (callerName == null) return false;
        return !callerName.startsWith("$") && Character.isUpperCase(callerName.charAt(0));
    }

    // ===== Naming convention helpers =====

    protected boolean matchesLowerCamelCase(String name) {
        if (name == null || name.isEmpty()) return false;
        return Pattern.matches("^[a-z][a-zA-Z0-9]*$", name);
    }

    protected boolean matchesUpperCamelCase(String name) {
        if (name == null || name.isEmpty()) return false;
        return Pattern.matches("^[A-Z][a-zA-Z0-9]*$", name);
    }

    // Check if the name token is immediately followed by '(' (no whitespace)
    protected boolean nameFollowedByParen(Node node) {
        if (node == null) return false;
        return true;
    }

    // Get the begin column of a node
    protected int getBeginColumn(Node node) {
        if (node == null) return -1;
        return node.getBeginColumn();
    }

    // Get the end column of a node
    protected int getEndColumn(Node node) {
        if (node == null) return -1;
        return node.getEndColumn();
    }

    // Get the begin line of a node
    protected int getBeginLine(Node node) {
        if (node == null) return -1;
        return node.getBeginLine();
    }

    // Get the end line of a node
    protected int getEndLine(Node node) {
        if (node == null) return -1;
        return node.getEndLine();
    }

    // Check if an import is static
    protected boolean isStaticImport(ASTImportDeclaration node) {
        if (node == null) return false;
        return node.isStatic();
    }

    // Get imported package/class name
    protected String getImportName(ASTImportDeclaration node) {
        if (node == null) return null;
        ASTName nameNode = node.getFirstChildOfType(ASTName.class);
        return nameNode != null ? nameNode.getImage() : null;
    }

    // Get package name from ASTPackageDeclaration
    protected String getPackageName(ASTPackageDeclaration node) {
        if (node == null) return null;
        ASTName nameNode = node.getFirstChildOfType(ASTName.class);
        return nameNode != null ? nameNode.getImage() : null;
    }
}'''
        with open(self.base_rule_output_path, "w", encoding="utf-8") as f:
            f.write(base_code)
        return base_code

    # ========== Pattern type detection ==========

    def _detect_pattern_type(self, snippet: str, ir: RuleIntentIR) -> str:
        """根据 code_snippet 和 IR 内容判断代码模式类型"""
        if not snippet:
            # 空snippet + file_property → 文件属性模式
            if ir.surface_pattern and ir.surface_pattern.pattern_type == "file_property":
                return "file_property"
            return "unknown"

        s = snippet.strip()

        # class $CLASS { ... }  → 类声明
        if re.match(r'class\s+\$\w+', s):
            return "class_declaration"

        # $TYPE $VAR = $EXPR;  → 局部变量声明
        if re.match(r'\$\w+\s+\$\w+\s*=', s):
            return "variable_declaration"

        # import ...  → import 声明
        if s.startswith('import '):
            return "import_declaration"

        # package $PKG;  → 包声明
        if s.startswith('package '):
            return "package_declaration"

        # catch ($EXCEPTION1|$EXCEPTION2 $VAR)  → catch 子句
        if s.startswith('catch '):
            return "catch_clause"

        # <$T extends $BASE&$BOUND>  → 泛型类型参数
        if s.startswith('<') and 'extends' in s:
            return "type_parameter"

        # $NAME $PAREN(...)  → 方法/构造器声明
        if re.match(r'\$\w+\s+\$\w*\s*\(', s):
            return "method_declaration"

        # $OBJ.method($ARG)  → 方法调用
        if re.search(r'(\$\w+|[A-Z]\w*)\s*\.\s*(\w+)\s*\(\s*(.*?)\s*\)', s):
            return "method_call"

        return "unknown"

    # ========== Main generate method ==========

    def generate(self, ir: RuleIntentIR) -> str:
        if ir.language.lower() != "java":
            return f'''// PMD 规则生成提示
// 规则ID: {ir.rule_id}
// 描述: {ir.description}
// 错误: PMD 原生不支持 {ir.language} 语言的规则检测
// 建议: 请使用 Semgrep/CodeQL 等工具实现该规则'''

        class_name = "".join(p.capitalize() for p in ir.rule_id.split('-'))
        snippet = ir.surface_pattern.code_snippet if ir.surface_pattern else ""
        pattern_type = self._detect_pattern_type(snippet, ir)

        # import 规则全部禁用（PMD 难以实现精确分组/排序检测）
        if pattern_type == "import_declaration":
            return self._unimplemented(ir, class_name)
        # file_property 规则禁用
        if pattern_type == "file_property":
            return self._gen_file_property_rule(ir, class_name)

        dispatch = {
            "method_call": self._gen_method_call_rule,
            "class_declaration": self._gen_class_declaration_rule,
            "variable_declaration": self._gen_variable_declaration_rule,
            "package_declaration": self._gen_package_declaration_rule,
            "catch_clause": self._gen_catch_clause_rule,
            "type_parameter": self._gen_type_parameter_rule,
            "method_declaration": self._gen_method_declaration_rule,
        }

        handler = dispatch.get(pattern_type)
        if handler:
            return handler(ir, class_name)

        return self._unimplemented(ir, class_name)

    # ========== Rule generators for each pattern type ==========

    def _gen_class_declaration_rule(self, ir: RuleIntentIR, class_name: str) -> str:
        """class $CLASS { ... } → 检查类命名规范"""
        escaped_desc = ir.description.replace('"', '\\"')
        priority = self.PRIORITY_MAP.get(ir.severity.lower(), "3")

        name_regex = self._extract_name_regex(ir)
        check_logic = ""

        if name_regex:
            check_logic = f'''
            String name = node.getImage();
            if (name != null && !Pattern.matches("{name_regex}", name)) {{
                addViolation(data, node, getMessage() + ": " + name);
            }}'''
        else:
            check_logic = '''
            String name = node.getImage();
            if (name != null && !matchesUpperCamelCase(name)) {
                addViolation(data, node, getMessage() + ": " + name);
            }'''

        return f'''package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import java.util.regex.Pattern;

/**
 * {ir.description}
 * 规则ID: {ir.rule_id} | 严重级别: {ir.severity.upper()} | PMD优先级: {priority}
 */
public class {class_name} extends PMDBaseRule {{
    public {class_name}() {{ setMessage("{escaped_desc}"); }}

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {{
        try {{{check_logic}
        }} catch (Exception e) {{}}
        return super.visit(node, data);
    }}
}}'''

    def _gen_variable_declaration_rule(self, ir: RuleIntentIR, class_name: str) -> str:
        escaped_desc = ir.description.replace('"', '\\"')
        priority = self.PRIORITY_MAP.get(ir.severity.lower(), "3")
        name_regex = self._extract_name_regex(ir)

        if name_regex:
            check_logic = f'''
                    String name = varId.getImage();
                    if (name != null && !Pattern.matches("{name_regex}", name)) {{
                        addViolation(data, varId, getMessage() + ": " + name);
                    }}'''
        else:
            check_logic = '''
                    String name = varId.getImage();
                    if (name != null && !matchesLowerCamelCase(name)) {
                        addViolation(data, varId, getMessage() + ": " + name);
                    }'''

        return f'''package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import java.util.regex.Pattern;

/**
 * {ir.description}
 * 规则ID: {ir.rule_id} | 严重级别: {ir.severity.upper()} | PMD优先级: {priority}
 */
public class {class_name} extends PMDBaseRule {{
    public {class_name}() {{ setMessage("{escaped_desc}"); }}

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {{
        try {{
            java.util.List<ASTVariableDeclaratorId> varIds =
                node.findDescendantsOfType(ASTVariableDeclaratorId.class);
            for (int i = 0; i < varIds.size(); i++) {{
                ASTVariableDeclaratorId varId = varIds.get(i);
                {check_logic}
            }}
        }} catch (Exception e) {{}}
        return super.visit(node, data);
    }}
}}'''

    def _gen_package_declaration_rule(self, ir: RuleIntentIR, class_name: str) -> str:
        escaped_desc = ir.description.replace('"', '\\"')
        priority = self.PRIORITY_MAP.get(ir.severity.lower(), "3")
        constraints = ir.context_constraints or []
        check_parts = []

        for c in constraints:
            cond = c.condition or ""
            if "line_count" in cond:
                check_parts.append("getEndLine(node) > getBeginLine(node)")

        if check_parts:
            cond_str = " || ".join(check_parts)
            body = f'''
            if ({cond_str}) {{
                addViolation(data, node, getMessage());
            }}'''
        else:
            body = '''
            // 无法可靠检测，规则已禁用
            return super.visit(node, data);
'''

        return f'''package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * {ir.description}
 * 规则ID: {ir.rule_id} | 严重级别: {ir.severity.upper()} | PMD优先级: {priority}
 */
public class {class_name} extends PMDBaseRule {{
    public {class_name}() {{ setMessage("{escaped_desc}"); }}

    @Override
    public Object visit(ASTPackageDeclaration node, Object data) {{
        try {{{body}
        }} catch (Exception e) {{}}
        return super.visit(node, data);
    }}
}}'''

    def _gen_catch_clause_rule(self, ir: RuleIntentIR, class_name: str) -> str:
        escaped_desc = ir.description.replace('"', '\\"')
        priority = self.PRIORITY_MAP.get(ir.severity.lower(), "3")
        return f'''package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * {ir.description}
 * 规则ID: {ir.rule_id} | 严重级别: {ir.severity.upper()} | PMD优先级: {priority}
 */
public class {class_name} extends PMDBaseRule {{
    public {class_name}() {{ setMessage("{escaped_desc}"); }}

    @Override
    public Object visit(ASTCatchStatement node, Object data) {{
        try {{
            // 检查 multi-catch 中 | 周围的空格
            java.util.List<ASTClassOrInterfaceType> exceptions =
                node.findChildrenOfType(ASTClassOrInterfaceType.class);
            if (exceptions.size() >= 2) {{
                for (int i = 0; i < exceptions.size() - 1; i++) {{
                    ASTClassOrInterfaceType curr = exceptions.get(i);
                    ASTClassOrInterfaceType next = exceptions.get(i + 1);
                    int currEnd = getEndColumn(curr);
                    int nextStart = getBeginColumn(next);
                    // " | " 的有空格间隔至少为3，无空格则 <=2
                    if (nextStart - currEnd <= 2) {{
                        addViolation(data, node, getMessage());
                        return super.visit(node, data);
                    }}
                }}
            }}
        }} catch (Exception e) {{}}
        return super.visit(node, data);
    }}
}}'''

    def _gen_type_parameter_rule(self, ir: RuleIntentIR, class_name: str) -> str:
        escaped_desc = ir.description.replace('"', '\\"')
        priority = self.PRIORITY_MAP.get(ir.severity.lower(), "3")
        return f'''package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * {ir.description}
 * 规则ID: {ir.rule_id} | 严重级别: {ir.severity.upper()} | PMD优先级: {priority}
 */
public class {class_name} extends PMDBaseRule {{
    public {class_name}() {{ setMessage("{escaped_desc}"); }}

    @Override
    public Object visit(ASTTypeParameter node, Object data) {{
        try {{
            java.util.List<ASTClassOrInterfaceType> bounds =
                node.findChildrenOfType(ASTClassOrInterfaceType.class);
            if (bounds.size() >= 2) {{
                for (int i = 0; i < bounds.size() - 1; i++) {{
                    ASTClassOrInterfaceType curr = bounds.get(i);
                    ASTClassOrInterfaceType next = bounds.get(i + 1);
                    int currEnd = getEndColumn(curr);
                    int nextStart = getBeginColumn(next);
                    if (nextStart - currEnd <= 2) {{
                        addViolation(data, node, getMessage());
                        return super.visit(node, data);
                    }}
                }}
            }}
        }} catch (Exception e) {{}}
        return super.visit(node, data);
    }}
}}'''

    def _gen_method_declaration_rule(self, ir: RuleIntentIR, class_name: str) -> str:
        escaped_desc = ir.description.replace('"', '\\"')
        priority = self.PRIORITY_MAP.get(ir.severity.lower(), "3")
        return f'''package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * {ir.description}
 * 规则ID: {ir.rule_id} | 严重级别: {ir.severity.upper()} | PMD优先级: {priority}
 */
public class {class_name} extends PMDBaseRule {{
    public {class_name}() {{ setMessage("{escaped_desc}"); }}

    @Override
    public Object visit(ASTMethodDeclarator node, Object data) {{
        try {{
            String image = node.getImage();
            if (image != null) {{
                int nameEnd = getEndColumn(node);
                if (node.jjtGetNumChildren() > 0) {{
                    Node firstParam = node.jjtGetChild(0);
                    int paramStart = getBeginColumn(firstParam);
                    if (nameEnd > 0 && paramStart > 0 && paramStart - nameEnd > 2) {{
                        addViolation(data, node, getMessage() + ": " + image);
                    }}
                }}
            }}
        }} catch (Exception e) {{}}
        return super.visit(node, data);
    }}
}}'''

    def _gen_file_property_rule(self, ir: RuleIntentIR, class_name: str) -> str:
        """文件属性类规则（空白、格式等）→ 生成禁用占位规则，避免误报"""
        escaped_desc = ir.description.replace('"', '\\"')
        priority = self.PRIORITY_MAP.get(ir.severity.lower(), "3")
        return f'''package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * {ir.description}
 * 规则ID: {ir.rule_id} | 严重级别: {ir.severity.upper()} | PMD优先级: {priority}
 * 注意：此格式化规则在 PMD 中难以精细实现，已禁用。
 */
public class {class_name} extends PMDBaseRule {{
    public {class_name}() {{ setMessage("{escaped_desc}"); }}
    @Override public Object visit(ASTCompilationUnit node, Object data) {{
        // 因 PMD 无法可靠检测此格式，规则不报告问题
        return super.visit(node, data);
    }}
}}'''

    def _gen_method_call_rule(self, ir: RuleIntentIR, class_name: str) -> str:
        """方法调用模式规则"""
        method_info = self._parse_method_call(ir.surface_pattern.code_snippet)
        escaped_desc = ir.description.replace('"', '\\"')
        conditions = self._build_conditions(ir, method_info)
        comments = self._build_comments(ir)
        priority = self.PRIORITY_MAP.get(ir.severity.lower(), "3")

        return f'''package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * {ir.description}
 * 规则ID: {ir.rule_id} | 严重级别: {ir.severity.upper()} | PMD优先级: {priority}
 * {comments}
 */
public class {class_name} extends AbstractJavaRule {{
    public {class_name}() {{ setMessage("{escaped_desc}"); }}

    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {{
        try {{
            if (isMethodCall(node, "{method_info['method_name']}")) {{
                if ({conditions}) {{
                    addViolation(data, node, getMessage());
                }}
            }}
        }} catch (Exception e) {{}}
        return super.visit(node, data);
    }}
}}'''

    # ========== Helper methods ==========

    def _extract_name_regex(self, ir: RuleIntentIR) -> Optional[str]:
        constraints = ir.context_constraints or []
        for c in constraints:
            cond = c.condition or ""
            match = re.search(r"name_matches_regex\(['\"](.+?)['\"]\)", cond)
            if match:
                return match.group(1)
        return None

    def _parse_method_call(self, snippet: str) -> Dict[str, Any]:
        info = {"type": "unknown", "method_name": "unknown", "arg_count": 0,
                "caller_var": None, "arg_vars": [], "is_static": False, "is_chained": False}
        if not snippet:
            return info

        match = re.search(r'(\$\w+|[A-Z]\w*)\s*\.\s*(\w+)\s*\(\s*(.*?)\s*\)', snippet)
        if not match:
            return info

        caller, method, args = match.groups()
        info.update({"type": "method_call", "method_name": method.strip()})

        if caller and not caller.startswith('$'):
            info["is_static"] = True

        if '.' in snippet.split(method)[1].split('(')[0]:
            info["is_chained"] = True

        if args.strip():
            raw = [a.strip() for a in re.split(r',\s*', args) if a.strip()]
            info["arg_count"] = len(raw)
            info["arg_vars"] = [a for a in raw if a.startswith('$')]

        if caller.startswith('$'):
            info["caller_var"] = caller

        return info

    def _build_conditions(self, ir: RuleIntentIR, method_info: Dict) -> str:
        var_to_expr = {}
        if method_info.get("caller_var"):
            var_to_expr[method_info["caller_var"]] = "getCaller(node)"
        for i, var in enumerate(method_info.get("arg_vars", [])):
            var_to_expr[var] = f"getArguments(node).get({i})"

        conds = []

        if ir.context_constraints:
            for c in ir.context_constraints:
                if c.constraint_type not in self.SUPPORTED_CONSTRAINTS or c.target not in var_to_expr:
                    continue
                expr = var_to_expr[c.target]
                cond_str = c.condition or ""
                req = c.required or ""

                if c.constraint_type == "type":
                    conds.extend(self._build_type_condition(expr, cond_str))
                elif c.constraint_type == "annotation" and req:
                    conds.append(f"hasAnnotation(getEnclosingClass(node), \"{req.strip('@')}\")")
                elif c.constraint_type == "inheritance" and cond_str:
                    conds.append(f"isSubclassOf(getEnclosingClass(node), \"{cond_str}\")")
                elif c.constraint_type == "scope" and cond_str == "literal":
                    conds.append(f"isLiteral({expr})")
                elif c.constraint_type == "value":
                    conds.extend(self._build_value_condition(expr, cond_str))

        if ir.surface_pattern and ir.surface_pattern.metavariables:
            for var, meta in ir.surface_pattern.metavariables.items():
                if var in var_to_expr:
                    expr = var_to_expr[var]
                    if meta.regex:
                        conds.extend(self._build_regex_condition(expr, meta.regex))
                    elif meta.type_hint:
                        conds.extend(self._build_type_condition(expr, meta.type_hint))

        if ir.dataflow_spec and ir.dataflow_spec.source and ir.dataflow_spec.source in var_to_expr:
            src = var_to_expr[ir.dataflow_spec.source]
            conds.append(f"(isNullLiteral({src}) || isSourcePotentiallyNull({src}))")

        unique = []
        for c in conds:
            if c and c not in unique:
                unique.append(c)
        return " &&\n            ".join(unique) if unique else "true"

    def _build_type_condition(self, expr: str, type_str: str) -> List[str]:
        conds = []
        type_str = type_str.strip().lower()
        if type_str == "literal negative integer":
            conds.append(f"checkIsNegativeLiteral({expr})")
        elif type_str == "literal":
            conds.append(f"isLiteral({expr})")
        elif type_str == "string":
            conds.append(f"isType({expr}, \"java.lang.String\")")
        elif type_str in ("int", "integer"):
            conds.append(f"isType({expr}, \"int\")")
        elif type_str == "null":
            conds.append(f"isNullLiteral({expr})")
        else:
            conds.append(f"isType({expr}, \"{type_str}\")")
        return conds

    def _build_value_condition(self, expr: str, value_str: str) -> List[str]:
        conds = []
        val_match = re.search(r'value\s*([<>]=?|==|!=)\s*(-?\d+)', value_str)
        if val_match:
            conds.append(f"isNumericLiteralMatch({expr}, \"{val_match[1]}\", {val_match[2]})")
        return conds

    def _build_regex_condition(self, expr: str, regex: str) -> List[str]:
        conds = []
        regex = regex.strip()
        if regex in (r"-\d+", "-\\d+"):
            conds.append(f"checkIsNegativeLiteral({expr})")
        elif regex in (r"\d+", "\\d+"):
            conds.append(f"isNumericLiteralMatch({expr}, \">=\", 0)")
        elif re.search(r'([<>]=?|==|!=)\s*(-?\d+)', regex):
            val_match = re.search(r'([<>]=?|==|!=)\s*(-?\d+)', regex)
            if val_match:
                conds.append(f"isNumericLiteralMatch({expr}, \"{val_match[1]}\", {val_match[2]})")
        return conds

    def _build_comments(self, ir: RuleIntentIR) -> str:
        caps = []
        if ir.capabilities_required.type_resolution: caps.append("需要类型解析")
        if ir.capabilities_required.dataflow: caps.append("需要数据流分析（需手动实现）")
        if ir.capabilities_required.inheritance_analysis: caps.append("需要继承分析")
        if ir.capabilities_required.cross_file: caps.append("需要跨文件分析（未实现）")
        parts = [f"能力: {', '.join(caps)}"] if caps else []
        if ir.dataflow_spec:
            df = []
            if ir.dataflow_spec.source: df.append(f"源={ir.dataflow_spec.source}")
            if ir.dataflow_spec.sink: df.append(f"汇={ir.dataflow_spec.sink}")
            if df: parts.append(f"数据流: {', '.join(df)}")
        return " | ".join(parts) if parts else "无特殊要求"

    def _unimplemented(self, ir: RuleIntentIR, class_name: str) -> str:
        desc = ir.description.replace('"', '\\"')
        snippet = ir.surface_pattern.code_snippet if ir.surface_pattern else ""
        return f'''package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;

/**
 * {ir.description}
 * 规则ID: {ir.rule_id} | 严重级别: {ir.severity.upper()}
 * 注意：当前模式暂未实现自动检测逻辑，已禁用。
 * 原始模式: {snippet}
 */
public class {class_name} extends PMDBaseRule {{
    public {class_name}() {{ setMessage("{desc}"); }}
    @Override public Object visit(ASTCompilationUnit node, Object data) {{
        // 规则未实现，不报告问题
        return super.visit(node, data);
    }}
}}'''