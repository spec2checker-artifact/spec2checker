package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Each continuation line must be indented at least +4 spaces from the original line.
 * 规则ID: java-continuation-indent | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaContinuationIndent extends PMDBaseRule {
    public JavaContinuationIndent() { setMessage("Each continuation line must be indented at least +4 spaces from the original line."); }
    @Override
    public Object visit(ASTMethodCall node, Object data) {
        ASTMemberExpression chain = node.getFirstChildOfType(ASTMemberExpression.class);
        if (chain != null) {
            int baseLine = node.getBeginLine();
            int baseColumn = node.getBeginColumn();
            ASTMemberExpression current = chain;
            while (current != null) {
                if (current.getBeginLine() > baseLine) {
                    if (current.getBeginColumn() < baseColumn + 4) {
                        addViolation(data, node);
                        break;
                    }
                    baseLine = current.getBeginLine();
                    baseColumn = current.getBeginColumn();
                }
                current = current.getFirstChildOfType(ASTMemberExpression.class);
            }
        }
        return super.visit(node, data);
    }
    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        return super.visit(node, data);
    }
}