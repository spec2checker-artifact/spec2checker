package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;

/**
 * Class members and initializers should follow a logical order that the maintainer could explain.
 * Basic check: detect overloaded methods (same name) that are not contiguous within a class.
 * 规则ID: java-class-logical-order | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaClassLogicalOrder extends PMDBaseRule {
    public JavaClassLogicalOrder() { setMessage("Class members should follow a logical order that the maintainer could explain, not chronological by date added."); }
    @Override public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        java.util.List<ASTMethodDeclaration> methods = node.findChildrenOfType(ASTMethodDeclaration.class);
        java.util.Map<String, java.util.List<Integer>> namePositions = new java.util.LinkedHashMap<>();
        for (int i = 0; i < methods.size(); i++) {
            String name = methods.get(i).getMethodName();
            namePositions.computeIfAbsent(name, k -> new java.util.ArrayList<>()).add(i);
        }
        for (java.util.Map.Entry<String, java.util.List<Integer>> entry : namePositions.entrySet()) {
            java.util.List<Integer> positions = entry.getValue();
            if (positions.size() > 1) {
                for (int i = 1; i < positions.size(); i++) {
                    if (positions.get(i) != positions.get(i - 1) + 1) {
                        addViolation(data, node);
                        return super.visit(node, data);
                    }
                }
            }
        }
        return super.visit(node, data);
    }
}