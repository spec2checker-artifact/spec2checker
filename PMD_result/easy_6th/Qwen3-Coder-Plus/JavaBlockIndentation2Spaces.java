package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Each new block increases indent by 2 spaces. When block ends, indent returns to previous level. Applies to code and comments.
 * Rule ID: java-block-indentation-2-spaces | Severity: WARNING | PMD Priority: 3
 */
public class JavaBlockIndentation2Spaces extends PMDBaseRule {
    public JavaBlockIndentation2Spaces() { setMessage("Each new block increases indent by 2 spaces. When block ends, indent returns to previous level. Applies to code and comments."); }
    @Override public Object visit(ASTBlock node, Object data) {
        if (node.getParent() instanceof ASTMethodDeclaration || node.getParent() instanceof ASTIfStatement || node.getParent() instanceof ASTForStatement || node.getParent() instanceof ASTWhileStatement || node.getParent() instanceof ASTTryStatement) {
            Node parent = node.getParent();
            int parentCol = parent.getBeginColumn();
            int blockCol = node.getBeginColumn();
            int expected = parentCol + 2;
            if (blockCol != expected && blockCol > parentCol) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }
}