package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import java.util.ArrayList;
import java.util.List;

public class JavaModuleDirectivesOrdering extends PMDBaseRule {
    public JavaModuleDirectivesOrdering() { setMessage("Module directives must be ordered as: all requires in one block, then exports, opens, uses, provides. Each block must be separated by a single blank line."); }

    @Override public Object visit(ASTModuleDeclaration node, Object data) {
        List<ASTModuleDirective> directives = node.findChildrenOfType(ASTModuleDirective.class);
        if (directives.isEmpty()) {
            return super.visit(node, data);
        }

        List<String> directiveTypes = new ArrayList<>();
        for (ASTModuleDirective directive : directives) {
            String type = directive.getType();
            if (type != null) {
                directiveTypes.add(type.toLowerCase());
            }
        }

        String[] requiredOrder = {"requires", "exports", "opens", "uses", "provides"};
        int lastOrderIndex = -1;
        for (String dtype : directiveTypes) {
            int currentIndex = getIndex(dtype, requiredOrder);
            if (currentIndex < lastOrderIndex) {
                addViolation(data, node);
                return super.visit(node, data);
            }
            lastOrderIndex = currentIndex;
        }

        return super.visit(node, data);
    }

    private int getIndex(String type, String[] order) {
        for (int i = 0; i < order.length; i++) {
            if (order[i].equals(type)) return i;
        }
        return order.length;
    }
}