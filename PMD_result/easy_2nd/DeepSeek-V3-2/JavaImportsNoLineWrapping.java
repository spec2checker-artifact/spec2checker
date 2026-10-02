package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.RuleContext;

/**
 * Imports are not line-wrapped. The column limit does not apply.
 * Detects import declarations that span multiple lines.
 * Rule ID: java-imports-no-line-wrapping | Severity: WARNING | PMD Priority: 3
 */
public class JavaImportsNoLineWrapping extends AbstractJavaRule {

    public JavaImportsNoLineWrapping() {
        setMessage("Imports are not line-wrapped. The column limit does not apply.");
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        int beginLine = node.getBeginLine();
        int endLine = node.getEndLine();
        if (endLine > beginLine) {
            addViolation((RuleContext) data, node);
        }
        return super.visit(node, data);
    }
}