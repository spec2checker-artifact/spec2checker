package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.RuleContext;

/**
 * Contents of switch block must be indented +2 relative to the switch statement.
 * 规则ID: java-switch-indentation | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaSwitchIndentation extends PMDBaseRule {
    public JavaSwitchIndentation() { setMessage("Contents of switch block must be indented +2 relative to the switch statement. Each switch label (case, default) must start at that indentation level."); }

    @Override public Object visit(ASTSwitchStatement node, Object data) {
        int switchIndent = node.getBeginLine() > 0 ? getLineIndent(node) : -1;
        if (switchIndent < 0) return super.visit(node, data);

        for (int i = 0; i < node.getNumChildren(); i++) {
            Object child = node.getChild(i);
            if (child instanceof ASTSwitchLabel) {
                ASTSwitchLabel label = (ASTSwitchLabel) child;
                int labelIndent = getLineIndent(label);
                if (labelIndent >= 0 && labelIndent != switchIndent + 2) {
                    addViolation(data, label);
                }
            }
        }
        return super.visit(node, data);
    }

    private int getLineIndent(Object node) {
        try {
            java.lang.reflect.Method method = node.getClass().getMethod("getBeginColumn");
            int col = (Integer) method.invoke(node);
            return col - 1;
        } catch (Exception e) {
            return -1;
        }
    }
}
