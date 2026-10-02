package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;

public class JavaNewStyleSwitchSingleLine extends PMDBaseRule {
    public JavaNewStyleSwitchSingleLine() { setMessage("New-style switch rule can be written on a single line if it follows Google style."); }
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
        if (hasArrow) addViolation(data, node);
        return super.visit(node, data);
    }
}