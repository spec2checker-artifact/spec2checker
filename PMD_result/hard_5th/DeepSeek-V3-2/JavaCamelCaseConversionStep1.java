package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;

/**
 * Convert phrase to plain ASCII and remove apostrophes.
 * Basic check: detect string literals containing apostrophes or non-ASCII characters in identifier contexts.
 * 规则ID: java-camel-case-conversion-step1 | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaCamelCaseConversionStep1 extends PMDBaseRule {
    public JavaCamelCaseConversionStep1() { setMessage("Convert phrase to plain ASCII and remove apostrophes. Example: 'Muellers algorithm' should become 'Muellers algorithm' (no apostrophe)."); }
    @Override public Object visit(ASTLiteral node, Object data) {
        String img = node.getImage();
        if (img != null && img.startsWith("\"") && img.endsWith("\"")) {
            String content = img.substring(1, img.length() - 1);
            if (content.contains("'") || !content.matches("[\\x00-\\x7F]*")) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }
}