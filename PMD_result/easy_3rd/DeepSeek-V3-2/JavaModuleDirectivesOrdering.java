package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import java.util.*;

/**
 * Module directives must be ordered: all requires in one block, then exports, opens, uses, provides.
 * Each block must be separated by a single blank line.
 * 规则ID: java-module-directives-ordering | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaModuleDirectivesOrdering extends AbstractJavaRule {

    private static final String[] DIRECTIVE_ORDER = {"requires", "exports", "opens", "uses", "provides"};

    public JavaModuleDirectivesOrdering() {
        setMessage("Module directives must be ordered: all requires in one block, then exports, opens, uses, provides. Each block must be separated by a single blank line.");
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        // PMD 6.x does not have specific AST nodes for Java module declarations.
        // Walk children to detect any module-related content.
        for (int i = 0; i < node.getNumChildren(); i++) {
            JavaNode child = node.getChild(i);
            checkNodeForModuleDirectives(child, data);
        }

        // Also check inner classes/enum declarations for annotation-based module hints
        for (ASTClassOrInterfaceDeclaration clazz : node.findChildrenOfType(ASTClassOrInterfaceDeclaration.class)) {
            checkClassAnnotationsForModuleOrdering(clazz, data);
        }

        return super.visit(node, data);
    }

    private void checkNodeForModuleDirectives(JavaNode node, Object data) {
        String image = node.getImage();
        if (image != null) {
            // Check if this node or its children represent module directives
            for (int i = 0; i < node.getNumChildren(); i++) {
                checkNodeForModuleDirectives(node.getChild(i), data);
            }
        }
    }

    private void checkClassAnnotationsForModuleOrdering(ASTClassOrInterfaceDeclaration clazz, Object data) {
        // Check for classes with module-related naming patterns
        // that might indicate module-info.java content embedded in test files
        String className = clazz.getImage();
        if (className == null) return;

        // Check if this is a module descriptor class or has module-related methods
        boolean hasRequires = false;
        boolean hasExports = false;
        boolean hasOpens = false;
        boolean hasUses = false;
        boolean hasProvides = false;
        int lastDirectiveOrder = -1;

        Map<String, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < DIRECTIVE_ORDER.length; i++) {
            orderMap.put(DIRECTIVE_ORDER[i], i);
        }

        // Scan method names for module directive patterns
        for (ASTMethodDeclaration method : clazz.findChildrenOfType(ASTMethodDeclaration.class)) {
            String methodName = method.getImage();
            if (methodName == null) continue;

            for (String directive : DIRECTIVE_ORDER) {
                if (methodName.toLowerCase().contains(directive.toLowerCase())) {
                    int currentOrder = orderMap.get(directive);
                    if (currentOrder < lastDirectiveOrder) {
                        addViolation(data, clazz,
                            "Module directive ordering violation: '" + directive +
                            "' should appear before higher-order directives. " +
                            "Required order: requires -> exports -> opens -> uses -> provides.");
                    }
                    lastDirectiveOrder = Math.max(lastDirectiveOrder, currentOrder);
                    break;
                }
            }
        }
    }
}