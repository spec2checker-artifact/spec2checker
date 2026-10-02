package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;

public class JavaLocalVariableDeclarationTiming extends PMDBaseRule {
    public JavaLocalVariableDeclarationTiming() { setMessage("Local variables should be declared close to their first use, not at the start of a block."); }
    @Override public Object visit(ASTBlock node, Object data) {
        java.util.List<ASTLocalVariableDeclaration> vars = node.findChildrenOfType(ASTLocalVariableDeclaration.class);
        if (vars.size() > 3) {
            int firstUseLine = node.getBeginLine();
            for (ASTLocalVariableDeclaration var : vars) {
                if (var.getBeginLine() - firstUseLine > 5) {
                    addViolation(data, var);
                }
            }
        }
        return super.visit(node, data);
    }
}