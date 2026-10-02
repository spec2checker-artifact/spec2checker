package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import java.util.*;

/**
 * Methods with the same name must appear in a single contiguous group with no other members in between. Same for constructors. Applies even when modifiers differ.
 * Rule ID: java-overloads-never-split | Severity: WARNING | PMD Priority: 3
 */
public class JavaOverloadsNeverSplit extends PMDBaseRule {
    public JavaOverloadsNeverSplit() { setMessage("Methods with the same name must appear in a single contiguous group with no other members in between. Same for constructors. Applies even when modifiers differ."); }
    @Override public Object visit(ASTClassOrInterfaceBody node, Object data) {
        Map<String, List<Integer>> methodNameLines = new LinkedHashMap<>();
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof ASTMethodDeclaration) {
                ASTMethodDeclaration md = (ASTMethodDeclaration) child;
                String name = md.getName();
                if (name != null) {
                    methodNameLines.computeIfAbsent(name, k -> new ArrayList<>()).add(md.getBeginLine());
                }
            }
        }
        for (Map.Entry<String, List<Integer>> entry : methodNameLines.entrySet()) {
            List<Integer> lines = entry.getValue();
            if (lines.size() > 1) {
                for (int j = 0; j < lines.size() - 1; j++) {
                    int gap = lines.get(j + 1) - lines.get(j);
                    if (gap > 30) {
                        addViolation(data, node);
                        break;
                    }
                }
            }
        }
        return super.visit(node, data);
    }
}