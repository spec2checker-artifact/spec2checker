package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Even code that fits within column limit may be line-wrapped at author's discretion. Tip: extract method or local variable to avoid wrapping.
 * 规则ID: java-line-wrapping-discretionary | 严重级别: WARNING | PMD优先级: 3
 * 注意：此风格规则过于严苛，已禁用。
 */
public class JavaLineWrappingDiscretionary extends PMDBaseRule {
    public JavaLineWrappingDiscretionary() { setMessage("Even code that fits within column limit may be line-wrapped at author's discretion. Tip: extract method or local variable to avoid wrapping."); }
    @Override public Object visit(ASTCompilationUnit node, Object data) {
        return super.visit(node, data);
    }
}