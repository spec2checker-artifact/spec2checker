package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.RuleContext;

/**
 * Even code that fits within column limit may be line-wrapped at author's discretion.
 * Tip: extract method or local variable to avoid wrapping.
 * 规则ID: java-line-wrapping-discretionary | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaLineWrappingDiscretionary extends PMDBaseRule {
    private static final int DEFAULT_LINE_LIMIT = 120;

    public JavaLineWrappingDiscretionary() { setMessage("Even code that fits within column limit may be line-wrapped at author's discretion. Tip: extract method or local variable to avoid wrapping."); }

    @Override public Object visit(ASTCompilationUnit node, Object data) {
        String source = null;
        try {
            java.lang.reflect.Method method = node.getClass().getMethod("getText");
            source = (String) method.invoke(node);
        } catch (Exception e) {
            return super.visit(node, data);
        }
        if (source == null) return super.visit(node, data);

        String[] lines = source.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int visibleLen = 0;
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == '\t') {
                    visibleLen += 4 - (visibleLen % 4);
                } else {
                    visibleLen++;
                }
            }
            if (visibleLen > DEFAULT_LINE_LIMIT) {
                RuleContext ctx = (RuleContext) data;
                addViolationWithMessage(data, node, "Line " + (i + 1) + " exceeds " + DEFAULT_LINE_LIMIT + " column limit (actual: " + visibleLen + ")");
            }
        }
        return super.visit(node, data);
    }
}
