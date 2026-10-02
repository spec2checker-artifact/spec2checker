package com.rule.pmd;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class JavaEnumLineBreakAfterComma extends PMDBaseRule {
    public JavaEnumLineBreakAfterComma() { setMessage("After a comma following an enum constant, a line break is optional. Additional blank lines (usually one) are allowed."); }
    @Override public Object visit(ASTEnumBody node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof ASTEnumConstant) {
                ASTEnumConstant enumConst = (ASTEnumConstant) child;
                int constLine = enumConst.getBeginLine();
                if (i + 1 < node.jjtGetNumChildren()) {
                    Node nextChild = node.jjtGetChild(i + 1);
                    if (nextChild instanceof ASTEnumConstant) {
                        int nextLine = ((ASTEnumConstant) nextChild).getBeginLine();
                        if (nextLine == constLine) {
                            addViolation(data, enumConst);
                        }
                    }
                }
            }
        }
        return super.visit(node, data);
    }
}