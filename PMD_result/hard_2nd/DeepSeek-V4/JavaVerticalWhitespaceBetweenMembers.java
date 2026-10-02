package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.RuleContext;

/**
 * A single blank line should appear between consecutive members or initializers of a class.
 * 规则ID: java-vertical-whitespace-between-members | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaVerticalWhitespaceBetweenMembers extends PMDBaseRule {
    public JavaVerticalWhitespaceBetweenMembers() { setMessage("A single blank line should appear between consecutive members or initializers of a class."); }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        checkClassMembers(node, data);
        return super.visit(node, data);
    }

    private void checkClassMembers(ASTClassOrInterfaceDeclaration clazz, Object data) {
        int prevEndLine = -1;
        for (int i = 0; i < clazz.jjtGetNumChildren(); i++) {
            Node child = clazz.jjtGetChild(i);
            if (child instanceof ASTFieldDeclaration ||
                child instanceof ASTMethodDeclaration ||
                child instanceof ASTConstructorDeclaration ||
                child instanceof ASTInitializer ||
                child instanceof ASTEnumDeclaration ||
                child instanceof ASTClassOrInterfaceDeclaration) {

                int curStartLine = child.getBeginLine();
                if (prevEndLine > 0 && curStartLine == prevEndLine + 1) {
                    addViolation(data, child);
                }
                prevEndLine = child.getEndLine();
            }
        }
    }
}