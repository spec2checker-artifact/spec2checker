package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Each new block increases indent by 2 spaces. When block ends, indent returns to previous level. Applies to code and comments.
 * 规则ID: java-block-indentation-2-spaces | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaBlockIndentation2Spaces extends PMDBaseRule {
    public JavaBlockIndentation2Spaces() { setMessage("Each new block increases indent by 2 spaces. When block ends, indent returns to previous level. Applies to code and comments."); }

    @Override
    public Object visit(ASTBlock node, Object data) {
        int blockIndent = node.getBeginColumn();
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            var child = node.jjtGetChild(i);
            if (child instanceof ASTBlockStatement) {
                int childIndent = child.getBeginColumn();
                if (childIndent != blockIndent + 2 && childIndent != blockIndent) {
                    addViolation(data, node, "Block statement at line " + child.getBeginLine() + " has incorrect indentation. Expected " + (blockIndent + 2) + " but found " + childIndent + ".");
                }
            }
        }
        return super.visit(node, data);
    }
}