package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * After a comma following an enum constant, a line break is optional. Additional blank lines (usually one) are allowed.
 * 规则ID: java-enum-line-break-after-comma | 严重级别: WARNING | PMD优先级: 3
 * 注：此规则过于严格，枚举常量同行放置是常见且可接受的写法，已禁用。
 */
public class JavaEnumLineBreakAfterComma extends PMDBaseRule {
    public JavaEnumLineBreakAfterComma() { setMessage("DISABLED: After a comma following an enum constant, a line break is optional. Additional blank lines (usually one) are allowed."); }
    @Override public Object visit(ASTCompilationUnit node, Object data) {
        return data;
    }
}
