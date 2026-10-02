package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;

public class JavaTypeUseAnnotationPosition extends PMDBaseRule {
    public JavaTypeUseAnnotationPosition() { setMessage("Type-use annotations must appear immediately before the annotated type and be meta-annotated with @Target(ElementType.TYPE_USE)."); }
    @Override public Object visit(ASTAnnotation node, Object data) {
        String name = node.getAnnotationName();
        if (name != null) {
            Node parent = node.jjtGetParent();
            if (parent instanceof ASTClassOrInterfaceBodyDeclaration ||
                parent instanceof ASTFieldDeclaration ||
                parent instanceof ASTLocalVariableDeclaration) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }
}