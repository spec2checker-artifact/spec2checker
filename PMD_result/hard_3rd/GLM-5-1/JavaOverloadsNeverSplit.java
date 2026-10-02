package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import java.util.*;

/**
 * Methods with the same name must appear in a single contiguous group with no other members in between. Same for constructors. Applies even when modifiers differ.
 * 规则ID: java-overloads-never-split | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaOverloadsNeverSplit extends PMDBaseRule {
    public JavaOverloadsNeverSplit() { setMessage("Methods with the same name must appear in a single contiguous group with no other members in between. Same for constructors. Applies even when modifiers differ."); }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        List<String> memberOrder = new ArrayList<>();
        for (ASTClassOrInterfaceBodyDeclaration bodyDecl : node.findChildrenOfType(ASTClassOrInterfaceBodyDeclaration.class)) {
            if (bodyDecl.getFirstChildOfType(ASTMethodDeclaration.class) != null) {
                ASTMethodDeclaration method = bodyDecl.getFirstChildOfType(ASTMethodDeclaration.class);
                String methodName = method.getName();
                memberOrder.add(methodName);
            } else if (bodyDecl.getFirstChildOfType(ASTConstructorDeclaration.class) != null) {
                memberOrder.add("<init>");
            } else {
                memberOrder.add("<other>");
            }
        }

        Map<String, List<Integer>> methodNamePositions = new LinkedHashMap<>();
        for (int i = 0; i < memberOrder.size(); i++) {
            String name = memberOrder.get(i);
            if (!name.equals("<init>") && !name.equals("<other>")) {
                methodNamePositions.computeIfAbsent(name, k -> new ArrayList<>()).add(i);
            }
        }

        boolean violated = false;
        for (Map.Entry<String, List<Integer>> entry : methodNamePositions.entrySet()) {
            List<Integer> positions = entry.getValue();
            if (positions.size() > 1) {
                int first = positions.get(0);
                int last = positions.get(positions.size() - 1);
                for (int i = first + 1; i < last; i++) {
                    if (!entry.getKey().equals(memberOrder.get(i))) {
                        violated = true;
                        break;
                    }
                }
            }
            if (violated) break;
        }

        if (violated) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }
}