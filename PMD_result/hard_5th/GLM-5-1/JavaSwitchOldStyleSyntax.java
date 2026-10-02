package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;

public class JavaSwitchOldStyleSyntax extends PMDBaseRule {
    public JavaSwitchOldStyleSyntax() { setMessage("Detect use of old-style colon syntax in switch statements where new-style arrow syntax is required."); }
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