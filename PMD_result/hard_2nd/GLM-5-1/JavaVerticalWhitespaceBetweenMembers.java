package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * A single blank line should appear between consecutive members or initializers of a class.
 * 规则ID: java-vertical-whitespace-between-members | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaVerticalWhitespaceBetweenMembers extends PMDBaseRule {
    public JavaVerticalWhitespaceBetweenMembers() { setMessage("A single blank line should appear between consecutive members or initializers of a class."); }
    @Override public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        int numChildren = node.getNumChildren();
        Node prevMember = null;
        int prevEndLine = -1;
        for (int i = 0; i < numChildren; i++) {
            Node child = node.getChild(i);
            if (child instanceof ASTFieldDeclaration ||
                child instanceof ASTMethodDeclaration ||
                child instanceof ASTConstructorDeclaration ||
                child instanceof ASTInitializer ||
                child instanceof ASTEnumDeclaration ||
                child instanceof ASTClassOrInterfaceDeclaration) {
                int beginLine = child.getBeginLine();
                if (prevEndLine > 0 && beginLine > prevEndLine + 1) {
                    int blankLines = beginLine - prevEndLine - 1;
                    if (blankLines != 1) {
                        addViolationWithMessage(data, child, "Expected exactly 1 blank line between class members, found " + blankLines + ".");
                    }
                }
                prevEndLine = child.getEndLine();
                prevMember = child;
            }
        }
        return super.visit(node, data);
    }
}