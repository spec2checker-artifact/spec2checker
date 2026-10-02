package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaModuleDirectivesOrdering extends PMDBaseRule {
    public JavaModuleDirectivesOrdering() { setMessage("Module directives must be ordered as: all requires in one block, then exports, opens, uses, provides. Each block must be separated by a single blank line."); }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            String simpleName = child.getClass().getSimpleName();
            if (simpleName.equals("ASTModuleDeclaration")) {
                checkModuleOrder(child, data);
            }
        }
        return super.visit(node, data);
    }

    private void checkModuleOrder(Node moduleNode, Object data) {
        List<String> types = new ArrayList<>();
        for (int i = 0; i < moduleNode.jjtGetNumChildren(); i++) {
            Node child = moduleNode.jjtGetChild(i);
            String name = child.getClass().getSimpleName();
            if (name.contains("Requires")) types.add("requires");
            else if (name.contains("Exports")) types.add("exports");
            else if (name.contains("Opens")) types.add("opens");
            else if (name.contains("Uses")) types.add("uses");
            else if (name.contains("Provides")) types.add("provides");
        }
        String[] expected = {"requires", "exports", "opens", "uses", "provides"};
        int lastIdx = -1;
        for (String t : types) {
            int idx = Arrays.asList(expected).indexOf(t);
            if (idx >= 0 && idx < lastIdx) {
                addViolation(data, moduleNode);
                return;
            }
            if (idx >= 0) lastIdx = idx;
        }
    }
}