package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;

public class JavaKrSingleStatementBlock extends PMDBaseRule {
    public JavaKrSingleStatementBlock() { setMessage("Detects a block used where a single statement is allowed, which is permitted by K&R style to limit local variable scope."); }
    @Override public Object visit(ASTBlock node, Object data) {
        Node parent = node.jjtGetParent();
        if (parent instanceof ASTIfStatement || parent instanceof ASTWhileStatement ||
            parent instanceof ASTForStatement || parent instanceof ASTDoStatement) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }
}
