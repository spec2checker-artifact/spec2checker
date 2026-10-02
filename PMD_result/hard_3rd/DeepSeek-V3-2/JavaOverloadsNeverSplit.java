package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import java.util.*;

public class JavaOverloadsNeverSplit extends PMDBaseRule {
    public JavaOverloadsNeverSplit() {
        setMessage("Methods with the same name must appear in a single contiguous group with no other members in between. Same for constructors. Applies even when modifiers differ.");
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        List<ASTMethodDeclaration> methods = new ArrayList<>();
        node.findDescendantsOfType(ASTMethodDeclaration.class, methods, false);

        if (methods.size() < 2) return data;

        LinkedHashMap<String, List<Integer>> nameToIndex = new LinkedHashMap<>();
        for (int i = 0; i < methods.size(); i++) {
            String name = getMethodName(methods.get(i));
            if (!name.isEmpty()) {
                nameToIndex.computeIfAbsent(name, k -> new ArrayList<>()).add(i);
            }
        }

        for (Map.Entry<String, List<Integer>> entry : nameToIndex.entrySet()) {
            List<Integer> indices = entry.getValue();
            if (indices.size() > 1) {
                int first = indices.get(0);
                int last = indices.get(indices.size() - 1);
                for (int pos = first + 1; pos < last; pos++) {
                    String nameAtPos = getMethodName(methods.get(pos));
                    if (!nameAtPos.equals(entry.getKey())) {
                        addViolation(data, node);
                        break;
                    }
                }
            }
        }

        return data;
    }

    private String getMethodName(ASTMethodDeclaration m) {
        ASTMethodDeclarator decl = m.getFirstChildOfType(ASTMethodDeclarator.class);
        return decl != null ? decl.getImage() : "";
    }
}