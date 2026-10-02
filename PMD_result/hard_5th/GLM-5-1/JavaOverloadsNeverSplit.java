package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;

public class JavaOverloadsNeverSplit extends PMDBaseRule {
    public JavaOverloadsNeverSplit() { setMessage("Methods with the same name must appear in a single contiguous group with no other members in between."); }
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