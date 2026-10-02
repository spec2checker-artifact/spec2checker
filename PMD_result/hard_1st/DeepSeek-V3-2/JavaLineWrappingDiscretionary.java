package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Even code that fits within column limit may be line-wrapped at author's discretion. Tip: extract method or local variable to avoid wrapping.
 * 规则ID: java-line-wrapping-discretionary | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaLineWrappingDiscretionary extends PMDBaseRule {
    public JavaLineWrappingDiscretionary() { setMessage("Even code that fits within column limit may be line-wrapped at author's discretion. Tip: extract method or local variable to avoid wrapping."); }
    @Override public Object visit(ASTPrimaryExpression node, Object data) {
        int startLine = node.getBeginLine();
        int endLine = node.getEndLine();
        if (endLine > startLine) {
            ASTPrimaryPrefix prefix = node.getFirstChildOfType(ASTPrimaryPrefix.class);
            if (prefix != null) {
                java.util.List<ASTPrimarySuffix> suffixes = node.findChildrenOfType(ASTPrimarySuffix.class);
                if (suffixes.size() >= 2) {
                    addViolation(data, node);
                }
            }
        }
        return super.visit(node, data);
    }
}