package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;

public class JavaBlockIndentation2Spaces extends PMDBaseRule {
    public JavaBlockIndentation2Spaces() {
        setMessage("Empty catch block in try/catch/finally is not allowed. In multi-block statement, concise empty block not allowed. Empty catch/finally blocks should contain a comment or code.");
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        if (isEmptyCatchOrTryBlock(node)) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }

    private boolean isEmptyCatchOrTryBlock(ASTBlock block) {
        Node parent = block.jjtGetParent();
        boolean isCatch = parent instanceof ASTCatchStatement;
        boolean isTry = parent instanceof ASTTryStatement;
        if (!isCatch && !isTry) {
            return false;
        }
        if (block.jjtGetNumChildren() == 0) {
            return true;
        }
        return false;
    }
}