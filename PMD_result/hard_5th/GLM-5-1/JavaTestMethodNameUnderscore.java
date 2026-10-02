package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import java.util.regex.Pattern;

public class JavaTestMethodNameUnderscore extends PMDBaseRule {
    public JavaTestMethodNameUnderscore() { setMessage("JUnit test method names may contain underscores to separate logical components, each component in lowerCamelCase."); }
    @Override public Object visit(ASTMethodDeclaration node, Object data) {
        boolean hasTestAnnotation = false;
        ASTClassOrInterfaceDeclaration clazz = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        if (clazz != null) {
            for (ASTAnnotation ann : clazz.findChildrenOfType(ASTAnnotation.class)) {
                if ("Test".equals(ann.getAnnotationName())) hasTestAnnotation = true;
            }
        }
        for (ASTAnnotation ann : node.findChildrenOfType(ASTAnnotation.class)) {
            if ("Test".equals(ann.getAnnotationName())) hasTestAnnotation = true;
        }
        if (hasTestAnnotation) {
            String name = node.getMethodName();
            if (name != null && name.contains("_")) {
                String[] parts = name.split("_");
                for (String part : parts) {
                    if (!part.isEmpty() && !Pattern.matches("^[a-z][a-zA-Z0-9]*$", part)) {
                        addViolation(data, node);
                        return super.visit(node, data);
                    }
                }
            }
        }
        return super.visit(node, data);
    }
}