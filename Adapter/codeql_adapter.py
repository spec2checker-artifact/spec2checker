import re
from LLM_to_IR.ir_model import RuleIntentIR


class CodeQLRuleGenerator:
    """Convert RuleIntentIR to a CodeQL query (as string)."""

    def generate(self, ir: RuleIntentIR) -> str:
        lang = ir.language.lower()
        comment = f"""/**\n * CodeQL 查询: {ir.rule_id}\n * 描述: {ir.description}\n * 严重级别: {ir.severity.upper()}\n *\n * 语言: {ir.language}\n */\n"""

        if lang == "java":
            return self._generate_java_query(ir, comment)
        elif lang == "python":
            return self._generate_python_query(ir, comment)
        elif lang == "c" or lang == "cpp":
            return self._generate_cpp_query(ir, comment)
        elif lang == "javascript" or lang == "typescript":
            return self._generate_js_query(ir, comment)
        else:
            return self._generate_unsupported_query(ir, comment)

    # ---------- Java ----------
    def _generate_java_query(self, ir: RuleIntentIR, comment: str) -> str:
        snippet = ir.surface_pattern.code_snippet
        method_name = self._extract_method_name(snippet)
        if not method_name:
            method_name = "unknown"

        # 确定参数变量名（假设第一个元变量是参数）
        arg_var = None
        arg_var_info = None
        for var, var_info in ir.surface_pattern.metavariables.items():
            arg_var = var
            arg_var_info = var_info
            break

        conditions = []

        # 处理 metavariables 中的 regex 约束
        if arg_var and arg_var_info and arg_var_info.regex:
            regex_pattern = arg_var_info.regex
            expr = "arg"
            # JSON 中的 "-\\d+" 会被解析为 "-\d+"（一个反斜杠）
            # 检查是否为负整数模式（包含 - 和 \d+）
            if '-' in regex_pattern and regex_pattern.endswith('d+'):
                conditions.append(f"        {expr} instanceof CompileTimeConstantExpr")
                conditions.append(f"        {expr}.(CompileTimeConstantExpr).getIntValue() < 0")
            elif regex_pattern.endswith('d+'):
                conditions.append(f"        {expr} instanceof CompileTimeConstantExpr")
                conditions.append(f"        {expr}.(CompileTimeConstantExpr).getIntValue() >= 0")

        if ir.context_constraints:
            for c in ir.context_constraints:
                if c.target == arg_var:
                    expr = "arg"
                    cond_str = c.condition or ""
                    if c.constraint_type == "type":
                        # 检查是否为数值表达式（例如 "value < 0"）
                        value_match = re.search(r'value\s*([<>]=?|==|!=)\s*(-?\d+)', cond_str)
                        if value_match:
                            op = value_match.group(1)
                            val = value_match.group(2)
                            ql_op = {"<": "<", ">": ">", "<=": "<=", ">=": ">=", "==": "=", "!=": "!="}.get(op, "=")
                            conditions.append(f"        {expr}.(CompileTimeConstantExpr).getIntValue() {ql_op} {val}")
                        else:
                            conditions.append(f'        {expr}.getType().hasName("{cond_str}")')
                    elif c.constraint_type == "scope" and cond_str == "literal":
                        conditions.append(f"        {expr} instanceof CompileTimeConstantExpr")
                    elif c.constraint_type == "value":
                        value_match = re.search(r'value\s*([<>]=?|==|!=)\s*(-?\d+)', cond_str)
                        if value_match:
                            op = value_match.group(1)
                            val = value_match.group(2)
                            ql_op = {"<": "<", ">": ">", "<=": "<=", ">=": ">=", "==": "=", "!=": "!="}.get(op, "=")
                            conditions.append(f"        {expr}.(CompileTimeConstantExpr).getIntValue() {ql_op} {val}")
                else:
                    conditions.append(f"        // {c.constraint_type} on {c.target}: {c.condition or c.required}")

        # 添加调用者类型检查的注释（如果有其他元变量）
        caller_var = None
        for var in ir.surface_pattern.metavariables:
            if var != arg_var:
                caller_var = var
                break
        if caller_var:
            conditions.append(f'        // TODO: check caller type for {caller_var} (e.g., getCaller().getType().hasQualifiedName("java.lang.String"))')

        where_clause = "\n    and\n".join(conditions) if conditions else "        true"

        query = f"""{comment}
import java

from Call call, Expr arg
where
    call.getCallee() instanceof Method and
    call.getCallee().(Method).hasName("{method_name}") and
    arg = call.getArgument(0) and
{where_clause}
select call, call.getFile().toString() + ":" + call.getLocation().getStartLine().toString(), "{ir.description}"
"""
        return query

    # ---------- Python ----------
    def _generate_python_query(self, ir: RuleIntentIR, comment: str) -> str:
            # 检查规则是否需要继承分析
            needs_inheritance = False
            if ir.context_constraints:
                for c in ir.context_constraints:
                    if c.constraint_type == "inheritance" and c.condition == "parent_class_method_not_found":
                        needs_inheritance = True
                        break

            if needs_inheritance:
                # 需要继承分析的查询：检查@override装饰器是否使用正确
                # 注意：CodeQL Python库对装饰器支持有限，这里使用类继承关系检查
                # 检查类中定义的方法，且该类有基类
                # 注意：这个查询可能会产生误报，因为无法准确检测装饰器
                query = f"""{comment}
import python

from Function f, Class c
where
    c.getAMethod() = f
    and exists(Class parent | parent.getASubclass() = c)
    and not exists(Function parentMethod |
        parentMethod.getName() = f.getName() and
        parentMethod.getScope() = parent.getAScope()
    )
select f, "{ir.description}"
"""
            else:
                # 生成基于规则的查询
                # 从IR中提取模式信息
                snippet = ir.surface_pattern.code_snippet if ir.surface_pattern else ""

                # 根据不同的规则类型生成不同的查询
                if "power features" in ir.description.lower():
                    # 检测强大的Python特性（元类、字节码等）
                    query = f"""{comment}
import python

from Function f
where
    f.getName().matches("%metaclass%")
    or f.getName().matches("%__import__%")
    or f.getName().matches("%exec%")
    or f.getName().matches("%eval%")
    or f.getName().matches("%__code__%")
    or f.getName().matches("%__getattr__%")
select f, "{ir.description}"
"""
                elif "global state" in ir.description.lower():
                    # 检测可变全局状态（修复 API 兼容性）
                    query = f"""{comment}
import python

from Variable v, Assign a
where
    v.getScope() instanceof Module
    and v.getAnAssignedVariable() = v
    and a.getATarget() = v
select v, "{ir.description}"
"""
                elif "import" in ir.description.lower():
                    # 检测导入相关规则（修复：ImportNode -> ImportStmt）
                    query = f"""{comment}
import python

from Import i
where
    i.getModule().getId().matches("%{snippet.strip()}%")
select i, "{ir.description}"
"""
                elif "rename" in ir.description.lower() and "import" in ir.description.lower():
                    # 检测 import ... as ... 重命名（修复：使用 NameNode）
                    query = f"""{comment}
import python

from Import i, NameNode alias
where
    alias.getScope() = i.getScope()
    and alias.getAUse() = i
    and not alias.getId() = i.getModule().getId()
select alias, "{ir.description}"
"""
                elif "lambda" in ir.description.lower():
                    # 检测lambda相关规则（修复：直接使用 Lambda，不通过 Function 包装）
                    query = f"""{comment}
import python

from Lambda l
where
    l.getBody().getLocation().getStartLine() > 10
select l, "{ir.description}"
"""
                elif "classmethod" in ir.description.lower():
                    # 检测类方法（修复：使用正确的装饰器 API）
                    query = f"""{comment}
import python

from Function f, Class c, NameNode decorator
where
    c.getAMethod() = f
    and decorator.getId() = "classmethod"
    and decorator.getScope() = f
select f, "{ir.description}"
"""
                elif "whitespace" in ir.description.lower() or "indentation" in ir.description.lower():
                    # 格式化规则，CodeQL不擅长处理，返回空查询
                    query = f"""{comment}
import python

from Comment c
where
    c.getText().matches("%TODO%")
select c, "This rule requires formatting checks which CodeQL cannot handle"
"""
                elif "none check" in ir.description.lower():
                    # 检测 None 检查（修复：使用 is 操作符）
                    query = f"""{comment}
import python

from Compare cmp, NameNode name
where
    cmp.getOp() instanceof Is
    and name.getId() = "None"
    and cmp.getAnOperand() = name
select cmp, "{ir.description}"
"""
                elif "binary operator" in ir.description.lower() and "whitespace" in ir.description.lower():
                    # 格式化规则 - 二元操作符空格
                    query = f"""{comment}
import python

from Comment c
where
    c.getText().matches("%TODO%")
select c, "Binary operator whitespace is a formatting issue, use Semgrep instead"
"""
                else:
                    # 默认查询：查找函数
                    query = f"""{comment}
import python

from Function f
where
    f.getName() != "__init__"
    and not f.getName().matches("test%")
select f, "{ir.description}"
"""

            return query

    # ---------- C/C++ ----------
    def _generate_cpp_query(self, ir: RuleIntentIR, comment: str) -> str:
        func_name = self._extract_method_name(ir.surface_pattern.code_snippet) or "unknown"
        query = f"""{comment}
import cpp

from FunctionCall fc
where
    fc.getTarget().hasName("{func_name}")
select fc, "{ir.description}"
"""
        return query

    # ---------- JavaScript/TypeScript ----------
    def _generate_js_query(self, ir: RuleIntentIR, comment: str) -> str:
        func_name = self._extract_method_name(ir.surface_pattern.code_snippet) or "unknown"
        query = f"""{comment}
import javascript

from CallExpr ce
where
    ce.getCallee().(VarAccess).getName() = "{func_name}"
select ce, "{ir.description}"
"""
        return query

    # ---------- 不支持的语言 ----------
    def _generate_unsupported_query(self, ir: RuleIntentIR, comment: str) -> str:
        return f"""{comment}
// CodeQL does not natively support {ir.language} queries.
// Please use Semgrep or other tools for this rule.
// Original pattern: {ir.surface_pattern.code_snippet}
"""

    # ---------- 辅助方法 ----------
    def _extract_method_name(self, snippet: str) -> str:
        """从代码片段中提取方法/函数名（支持 $VAR 形式，返回具体名称）。"""
        match = re.search(r'(?:\$?\w+\.)?(\$?\w+)\s*\(', snippet)
        if match:
            name = match.group(1)
            if name.startswith('$'):
                return None  # 元变量无法直接用于 hasName
            return name
        return None