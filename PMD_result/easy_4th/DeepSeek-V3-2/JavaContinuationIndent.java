package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Each continuation line must be indented at least +4 spaces from the original line.
 * 规则ID: java-continuation-indent | 严重级别: WARNING | PMD优先级: 3
 * 注意：此格式化规则在 PMD 中难以精细实现，已禁用。
 */
public class JavaContinuationIndent extends PMDBaseRule {
    public JavaContinuationIndent() { setMessage("Each continuation line must be indented at least +4 spaces from the original line."); }
    @Override public Object visit(ASTCompilationUnit node, Object data) {
        // 因 PMD 无法可靠检测此格式，规则不报告问题
        return super.visit(node, data);
    }
}