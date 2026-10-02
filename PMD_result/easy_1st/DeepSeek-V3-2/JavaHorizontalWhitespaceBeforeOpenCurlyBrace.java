package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;

public class JavaHorizontalWhitespaceBeforeOpenCurlyBrace extends PMDBaseRule {
    public JavaHorizontalWhitespaceBeforeOpenCurlyBrace() { setMessage("Require a space before any open curly brace {, except in @SomeAnnotation({a, b}) and String[][] x = {{\"foo\"}};"); }

    @Override public Object visit(ASTBlock node, Object data) {
        Node parent = node.getParent();
        if (parent instanceof ASTAnnotation || parent instanceof ASTArrayInitializer || parent instanceof ASTMemberValueArrayInitializer) {
            return super.visit(node, data);
        }
        int blockCol = node.getBeginColumn();
        int parentEndCol = parent.getEndColumn();
        if (blockCol == parentEndCol + 1) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }

    @Override public Object visit(ASTClassOrInterfaceBody node, Object data) {
        Node parent = node.getParent();
        if (parent instanceof ASTAnnotation) {
            return super.visit(node, data);
        }
        int bodyCol = node.getBeginColumn();
        int parentEndCol = parent.getEndColumn();
        if (bodyCol == parentEndCol + 1) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }

    @Override public Object visit(ASTEnumBody node, Object data) {
        Node parent = node.getParent();
        int bodyCol = node.getBeginColumn();
        int parentEndCol = parent.getEndColumn();
        if (bodyCol == parentEndCol + 1) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }

    @Override public Object visit(ASTAnnotationTypeBody node, Object data) {
        Node parent = node.getParent();
        int bodyCol = node.getBeginColumn();
        int parentEndCol = parent.getEndColumn();
        if (bodyCol == parentEndCol + 1) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }

    @Override public Object visit(ASTRecordBody node, Object data) {
        Node parent = node.getParent();
        int bodyCol = node.getBeginColumn();
        int parentEndCol = parent.getEndColumn();
        if (bodyCol == parentEndCol + 1) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }

    @Override public Object visit(ASTInitializer node, Object data) {
        ASTBlock block = node.getFirstChildOfType(ASTBlock.class);
        if (block != null) {
            int blockCol = block.getBeginColumn();
            int initEndCol = node.getEndColumn();
            if (blockCol == initEndCol + 1) {
                addViolation(data, block);
            }
        }
        return super.visit(node, data);
    }

    @Override public Object visit(ASTArrayInitializer node, Object data) {
        return super.visit(node, data);
    }
}