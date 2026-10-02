package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Module directives must be ordered as: all requires in one block, then exports, opens, uses, provides. Each block must be separated by a single blank line.
 * 规则ID: java-module-directives-ordering | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaModuleDirectivesOrdering extends PMDBaseRule {
    public JavaModuleDirectivesOrdering() { setMessage("Module directives must be ordered as: all requires in one block, then exports, opens, uses, provides. Each block must be separated by a single blank line."); }

    @Override public Object visit(ASTCompilationUnit node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof ASTModuleDeclaration) {
                checkModuleDirectiveOrder((ASTModuleDeclaration) child, data);
            }
        }
        return super.visit(node, data);
    }

    private void checkModuleDirectiveOrder(ASTModuleDeclaration module, Object data) {
        String[] expectedOrder = {"requires", "exports", "opens", "uses", "provides"};
        int lastSeenIndex = -1;
        for (int i = 0; i < module.jjtGetNumChildren(); i++) {
            Node directive = module.jjtGetChild(i);
            String directiveName = getDirectiveTypeName(directive);
            if (directiveName == null) continue;
            int currentIndex = -1;
            for (int j = 0; j < expectedOrder.length; j++) {
                if (expectedOrder[j].equals(directiveName)) { currentIndex = j; break; }
            }
            if (currentIndex == -1) continue;
            if (currentIndex < lastSeenIndex) {
                addViolation(data, directive);
            }
            lastSeenIndex = currentIndex;
        }
    }

    private String getDirectiveTypeName(Node directive) {
        String className = directive.getClass().getSimpleName();
        if (className.contains("Requires")) return "requires";
        if (className.contains("Exports")) return "exports";
        if (className.contains("Opens")) return "opens";
        if (className.contains("Uses")) return "uses";
        if (className.contains("Provides")) return "provides";
        return null;
    }
}
