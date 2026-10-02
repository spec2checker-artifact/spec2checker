package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Methods with the same name must appear in a single contiguous group with no other members in between. Same for constructors. Applies even when modifiers differ.
 * 规则ID: java-overloads-never-split | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaOverloadsNeverSplit extends PMDBaseRule {
    public JavaOverloadsNeverSplit() { setMessage("Methods with the same name must appear in a single contiguous group with no other members in between. Same for constructors. Applies even when modifiers differ."); }
    @Override public Object visit(ASTClassOrInterfaceBody node, Object data) {
        String lastMethodName = null;
        java.util.Set<String> seenNames = new java.util.HashSet<String>();
        java.util.Set<String> splitNames = new java.util.HashSet<String>();
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Object child = node.jjtGetChild(i);
            if (child instanceof ASTMethodDeclarator) {
                ASTMethodDeclarator method = (ASTMethodDeclarator) child;
                String name = method.getImage();
                if (seenNames.contains(name) && !name.equals(lastMethodName)) {
                    splitNames.add(name);
                }
                seenNames.add(name);
                lastMethodName = name;
            } else {
                lastMethodName = null;
            }
        }
        if (!splitNames.isEmpty()) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }
}