package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Module directives must be ordered: all requires in one block, then exports, opens, uses, provides. Each block must be separated by a single blank line.
 * 规则ID: java-module-directives-ordering | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaModuleDirectivesOrdering extends PMDBaseRule {
    public JavaModuleDirectivesOrdering() { setMessage("Module directives must be ordered: all requires in one block, then exports, opens, uses, provides. Each block must be separated by a single blank line."); }
    @Override public Object visit(ASTCompilationUnit node, Object data) {
        for (int i = 0; i < node.getNumChildren(); i++) {
            if (node.getChild(i) instanceof ASTModuleDeclaration) {
                ASTModuleDeclaration moduleDecl = (ASTModuleDeclaration) node.getChild(i);
                boolean foundRequires = false;
                boolean foundExports = false;
                boolean foundOpens = false;
                boolean foundUses = false;
                boolean foundProvides = false;
                boolean orderingViolation = false;
                for (int j = 0; j < moduleDecl.getNumChildren(); j++) {
                    Object child = moduleDecl.getChild(j);
                    String childType = child.getClass().getSimpleName();
                    if (childType.contains("Requires")) { foundRequires = true; if (foundExports || foundOpens || foundUses || foundProvides) { orderingViolation = true; break; } }
                    else if (childType.contains("Exports")) { foundExports = true; if (foundOpens || foundUses || foundProvides) { orderingViolation = true; break; } }
                    else if (childType.contains("Opens")) { foundOpens = true; if (foundUses || foundProvides) { orderingViolation = true; break; } }
                    else if (childType.contains("Uses")) { foundUses = true; if (foundProvides) { orderingViolation = true; break; } }
                    else if (childType.contains("Provides")) { foundProvides = true; }
                }
                if (orderingViolation) { addViolation(data, moduleDecl); }
            }
        }
        return super.visit(node, data);
    }
}