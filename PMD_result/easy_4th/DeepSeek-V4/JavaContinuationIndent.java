package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Each continuation line must be indented at least +4 spaces from the original line.
 * 规则ID: java-continuation-indent | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaContinuationIndent extends PMDBaseRule {
    public JavaContinuationIndent() { setMessage("Each continuation line must be indented at least +4 spaces from the original line."); }

    @Override public Object visit(ASTMethodDeclarator node, Object data) {
        int nameLine = node.getBeginLine();
        int nameCol = node.getBeginColumn();
        if (node.jjtGetNumChildren() > 0) {
            Node firstParam = node.jjtGetChild(0);
            if (firstParam.getBeginLine() > nameLine) {
                int paramCol = firstParam.getBeginColumn();
                if (paramCol < nameCol + 4) {
                    addViolation(data, node);
                }
            }
        }
        return super.visit(node, data);
    }
}