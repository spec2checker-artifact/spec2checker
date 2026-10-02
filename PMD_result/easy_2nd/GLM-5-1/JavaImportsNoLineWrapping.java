package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Imports are not line-wrapped. The column limit does not apply.
 * 规则ID: java-imports-no-line-wrapping | 严重级别: WARNING | PMD优先级: 3
 * Detects import declarations that span multiple lines (line-wrapped imports).
 */
public class JavaImportsNoLineWrapping extends PMDBaseRule {
    public JavaImportsNoLineWrapping() { setMessage("Imports are not line-wrapped. The column limit does not apply."); }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        int beginLine = node.getBeginLine();
        int endLine = node.getEndLine();
        if (beginLine != endLine) {
            addViolationWithMessage(data, node, "Import declaration spans multiple lines and should not be line-wrapped: " + getImportName(node));
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        return super.visit(node, data);
    }
}