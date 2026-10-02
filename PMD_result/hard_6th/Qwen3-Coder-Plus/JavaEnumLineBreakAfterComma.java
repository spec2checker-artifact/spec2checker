package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * After a comma following an enum constant, a line break is optional. Additional blank lines (usually one) are allowed.
 * 规则ID: java-enum-line-break-after-comma | 严重级别: WARNING | PMD优先级: 3
 * 注意：此格式化规则在 PMD 中难以精细实现，已禁用。
 */
public class JavaEnumLineBreakAfterComma extends PMDBaseRule {
    public JavaEnumLineBreakAfterComma() { setMessage("After a comma following an enum constant, a line break is optional. Additional blank lines (usually one) are allowed."); }
    @Override public Object visit(ASTCompilationUnit node, Object data) {
        // 因 PMD 无法可靠检测此格式，规则不报告问题
        return super.visit(node, data);
    }
}