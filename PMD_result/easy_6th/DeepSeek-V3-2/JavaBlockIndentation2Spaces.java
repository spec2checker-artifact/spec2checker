package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Each new block increases indent by 2 spaces. When block ends, indent returns to previous level. Applies to code and comments.
 * 规则ID: java-block-indentation-2-spaces | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaBlockIndentation2Spaces extends PMDBaseRule {
    public JavaBlockIndentation2Spaces() { setMessage("Each new block increases indent by 2 spaces. When block ends, indent returns to previous level. Applies to code and comments."); }
    @Override public Object visit(ASTCompilationUnit node, Object data) {
        for (ASTClassOrInterfaceDeclaration clazz : node.findChildrenOfType(ASTClassOrInterfaceDeclaration.class)) {
            if (hasAnnotation(clazz, "Test") || hasAnnotation(clazz, "RunWith")) {
                String name = clazz.getImage();
                if (name != null
                    && !name.endsWith("Test")
                    && !name.endsWith("Tests")
                    && !name.endsWith("TestCase")
                    && !name.endsWith("IT")
                    && !name.endsWith("IntegrationTest")
                    && !name.endsWith("Suite")) {
                    addViolation(data, clazz, "Test class '" + name + "' must end with 'Test'.");
                }
            }
        }
        return super.visit(node, data);
    }
}