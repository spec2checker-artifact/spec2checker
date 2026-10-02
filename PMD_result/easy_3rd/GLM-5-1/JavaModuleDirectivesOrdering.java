package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;

/**
 * Module directives must be ordered: all requires in one block, then exports, opens, uses, provides.
 * 规则ID: java-module-directives-ordering | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaModuleDirectivesOrdering extends PMDBaseRule {
    public JavaModuleDirectivesOrdering() { setMessage("Module directives must be ordered: all requires in one block, then exports, opens, uses, provides. Each block must be separated by a single blank line."); }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        checkImports(node, data);
        checkAnnotations(node, data);
        return super.visit(node, data);
    }

    private void checkImports(ASTCompilationUnit node, Object data) {
        int lastStaticImportCount = 0;
        int lastRegularImportCount = 0;
        boolean foundStatic = false;
        boolean foundRegular = false;
        for (ASTImportDeclaration imp : node.findChildrenOfType(ASTImportDeclaration.class)) {
            if (isStaticImport(imp)) {
                if (foundRegular) {
                    addViolation(data, imp);
                }
                foundStatic = true;
                lastStaticImportCount++;
            } else {
                foundRegular = true;
                lastRegularImportCount++;
            }
        }
    }

    private void checkAnnotations(ASTCompilationUnit node, Object data) {
        for (ASTAnnotation ann : node.findChildrenOfType(ASTAnnotation.class)) {
            String name = ann.getAnnotationName();
            if ("Module".equals(name) || "module".equals(name)) {
                ASTClassOrInterfaceDeclaration parent = getEnclosingClass(ann);
                if (parent != null) {
                    checkClassMemberOrdering(parent, data);
                }
            }
        }
    }

    private void checkClassMemberOrdering(ASTClassOrInterfaceDeclaration clazz, Object data) {
        String[] memberOrder = {"field", "constructor", "method"};
        int lastType = -1;
        for (int i = 0; i < clazz.jjtGetNumChildren(); i++) {
            Object child = clazz.jjtGetChild(i);
            int type = -1;
            if (child instanceof ASTFieldDeclaration) type = 0;
            else if (child instanceof ASTConstructorDeclaration) type = 1;
            else if (child instanceof ASTMethodDeclaration) type = 2;
            if (type >= 0 && type < lastType) {
                addViolation(data, clazz.jjtGetChild(i));
            }
            if (type >= 0) lastType = type;
        }
    }
}