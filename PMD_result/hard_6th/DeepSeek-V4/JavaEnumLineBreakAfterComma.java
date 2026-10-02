package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;

/**
 * After a comma following an enum constant, a line break is optional. Additional blank lines (usually one) are allowed.
 * Rule ID: java-enum-line-break-after-comma | Severity: WARNING | PMD Priority: 3
 */
public class JavaEnumLineBreakAfterComma extends PMDBaseRule {
    public JavaEnumLineBreakAfterComma() { setMessage("After a comma following an enum constant, a line break is optional. Additional blank lines (usually one) are allowed."); }
    @Override public Object visit(ASTCompilationUnit node, Object data) {
        return data;
    }
}