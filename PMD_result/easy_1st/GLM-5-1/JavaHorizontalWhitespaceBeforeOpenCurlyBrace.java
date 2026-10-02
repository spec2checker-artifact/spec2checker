package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class JavaHorizontalWhitespaceBeforeOpenCurlyBrace extends PMDBaseRule {
    public JavaHorizontalWhitespaceBeforeOpenCurlyBrace() { setMessage("Require a space before any open curly brace {, except in @SomeAnnotation({a, b}) and String[][] x = {{\"foo\"}};"); }

    @Override
    public Object visit(ASTBlock node, Object data) {
        Node parent = node.jjtGetParent();
        if (parent == null) return super.visit(node, data);

        if (parent instanceof ASTAnnotation ||
            parent instanceof ASTArrayInitializer ||
            parent instanceof ASTClassOrInterfaceBody ||
            parent instanceof ASTClassOrInterfaceBodyDeclaration ||
            parent instanceof ASTEnumBody ||
            parent instanceof ASTEnumDeclaration ||
            parent instanceof ASTClassOrInterfaceDeclaration) {
            return super.visit(node, data);
        }

        int blockLine = node.getBeginLine();
        int blockCol = node.getBeginColumn();

        if (parent instanceof ASTTryStatement || parent instanceof ASTFinallyStatement || parent instanceof ASTInitializer) {
            if (parent.getBeginLine() == blockLine) {
                String kw = parent instanceof ASTTryStatement ? "try" : (parent instanceof ASTFinallyStatement ? "finally" : "static");
                int kwEndCol = parent.getBeginColumn() + kw.length() - 1;
                if (blockCol == kwEndCol + 1) {
                    addViolation(data, node);
                }
            }
            return super.visit(node, data);
        }

        if (parent instanceof ASTMethodDeclaration) {
            ASTMethodDeclaration md = (ASTMethodDeclaration) parent;
            ASTMethodDeclarator declarator = md.getMethodDeclarator();
            if (declarator != null && declarator.getEndLine() == blockLine) {
                int gap = blockCol - declarator.getEndColumn();
                if (gap == 1) {
                    addViolation(data, node);
                }
            }
            return super.visit(node, data);
        }

        Node prevSibling = null;
        for (int i = 0; i < parent.jjtGetNumChildren(); i++) {
            if (parent.jjtGetChild(i) == node && i > 0) {
                prevSibling = parent.jjtGetChild(i - 1);
                break;
            }
        }

        if (prevSibling != null && prevSibling.getEndLine() == blockLine) {
            int prevEndCol = prevSibling.getEndColumn();
            int gap = blockCol - prevEndCol;
            if (parent instanceof ASTIfStatement || parent instanceof ASTWhileStatement || parent instanceof ASTForStatement || parent instanceof ASTCatchStatement) {
                if (gap == 1 || gap == 2) {
                    addViolation(data, node);
                }
                if (parent instanceof ASTIfStatement && prevSibling instanceof ASTBlock && gap == 5) {
                    addViolation(data, node);
                }
            } else if (gap == 1) {
                addViolation(data, node);
            }
        }

        return super.visit(node, data);
    }
}
