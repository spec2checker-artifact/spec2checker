package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import java.util.regex.Pattern;

/**
 * After a comma following an enum constant, a line break is optional. Additional blank lines (usually one) are allowed.
 * 规则ID: java-enum-line-break-after-comma | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaEnumLineBreakAfterComma extends PMDBaseRule {
    public JavaEnumLineBreakAfterComma() { setMessage("After a comma following an enum constant, a line break is optional. Additional blank lines (usually one) are allowed."); }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof ASTEnumBody) {
                checkEnumBody((ASTEnumBody) child, data);
            }
        }
        return super.visit(node, data);
    }

    private void checkEnumBody(ASTEnumBody enumBody, Object data) {
        int prevLine = -1;
        for (int i = 0; i < enumBody.jjtGetNumChildren(); i++) {
            Node child = enumBody.jjtGetChild(i);
            if (child instanceof ASTEnumConstant) {
                ASTEnumConstant constant = (ASTEnumConstant) child;
                int curLine = constant.getBeginLine();
                if (prevLine > 0 && curLine == prevLine) {
                    addViolation(data, constant);
                }
                prevLine = curLine;
            }
        }
    }
}