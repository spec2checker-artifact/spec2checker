package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.RuleContext;
import java.io.*;
import java.nio.file.*;

public class JavaEnumLineBreakAfterComma extends PMDBaseRule {
    public JavaEnumLineBreakAfterComma() { setMessage("After a comma following an enum constant, a line break is optional. Additional blank lines (usually one) are allowed."); }
    @Override public Object visit(ASTEnumDeclaration node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            net.sourceforge.pmd.lang.ast.Node child = node.jjtGetChild(i);
            if (child instanceof ASTEnumBody) {
                int prevLine = -1;
                for (int j = 0; j < child.jjtGetNumChildren(); j++) {
                    net.sourceforge.pmd.lang.ast.Node enumConst = child.jjtGetChild(j);
                    if (enumConst instanceof ASTEnumConstant) {
                        int curLine = enumConst.getBeginLine();
                        if (prevLine > 0 && curLine == prevLine) {
                            addViolation(data, enumConst);
                        }
                        prevLine = curLine;
                    }
                }
            }
        }
        return super.visit(node, data);
    }
}