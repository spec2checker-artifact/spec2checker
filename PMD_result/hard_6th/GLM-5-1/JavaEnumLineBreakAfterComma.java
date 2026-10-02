package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * After a comma following an enum constant, a line break is optional. Additional blank lines (usually one) are allowed.
 * 规则ID: java-enum-line-break-after-comma | 严重级别: WARNING | PMD优先级: 3
 * DISABLED: All 59 findings were false positives. The rule was too strict - each enum constant on its own line is valid practice.
 */
public class JavaEnumLineBreakAfterComma extends PMDBaseRule {
    public JavaEnumLineBreakAfterComma() { setMessage("DISABLED: After a comma following an enum constant, a line break is optional. Additional blank lines (usually one) are allowed."); }
    @Override public Object visit(ASTCompilationUnit node, Object data) {
        return data;
    }
}