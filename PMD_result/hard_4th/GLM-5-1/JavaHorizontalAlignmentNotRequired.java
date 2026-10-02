package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import java.util.ArrayList;
import java.util.List;

/**
 * Horizontal alignment of tokens using variable spaces is permitted but never required.
 * Developers are not required to maintain existing alignment.
 * 规则ID: java-horizontal-alignment-not-required | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaHorizontalAlignmentNotRequired extends PMDBaseRule {
    public JavaHorizontalAlignmentNotRequired() { setMessage("Horizontal alignment of tokens using variable spaces is permitted but never required. Developers are not required to maintain existing alignment."); }

    @Override public Object visit(ASTClassOrInterfaceBody node, Object data) {
        List<ASTFieldDeclaration> fields = node.findChildrenOfType(ASTFieldDeclaration.class);
        if (fields.size() < 2) return super.visit(node, data);

        int prevAssignCol = -1;
        int alignedCount = 0;
        for (ASTFieldDeclaration field : fields) {
            ASTVariableDeclarator decl = field.getFirstChildOfType(ASTVariableDeclarator.class);
            if (decl == null) continue;
            int assignCol = decl.getBeginColumn();
            if (prevAssignCol > 0 && assignCol == prevAssignCol) {
                alignedCount++;
            }
            prevAssignCol = assignCol;
        }
        if (alignedCount >= 3) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }
}
