package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Require a space before any open curly brace {, except in @SomeAnnotation({a, b}) and String[][] x = {{"foo"}};
 * 规则ID: java-horizontal-whitespace-before-open-curly-brace | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaHorizontalWhitespaceBeforeOpenCurlyBrace extends PMDBaseRule {
    public JavaHorizontalWhitespaceBeforeOpenCurlyBrace() { setMessage("Require a space before any open curly brace {, except in @SomeAnnotation({a, b}) and String[][] x = {{\"foo\"}};"); }
    @Override public Object visit(ASTBlock node, Object data) {
        if (node.getParent() instanceof ASTMethodDeclaration || node.getParent() instanceof ASTIfStatement || node.getParent() instanceof ASTForStatement || node.getParent() instanceof ASTWhileStatement || node.getParent() instanceof ASTTryStatement) {
            int blockCol = node.getBeginColumn();
            Node parent = node.getParent();
            int parentEndCol = parent.getEndColumn();
            if (blockCol == parentEndCol + 1) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }
}