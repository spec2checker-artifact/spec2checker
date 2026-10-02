package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;

public class JavaSwitchBlockStyleMix extends PMDBaseRule {
    public JavaSwitchBlockStyleMix() { setMessage("Detect mixing of new-style switch rules (label ->) and old-style statement groups (label:) within the same switch block."); }
    @Override public Object visit(ASTSwitchStatement node, Object data) {
        boolean hasArrow = false;
        boolean hasColon = false;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof ASTSwitchLabel) {
                ASTSwitchLabel label = (ASTSwitchLabel) child;
                if (label.getImage() != null && label.getImage().contains("->")) hasArrow = true;
                else hasColon = true;
            }
        }
        if (hasArrow && hasColon) addViolation(data, node);
        return super.visit(node, data);
    }
}