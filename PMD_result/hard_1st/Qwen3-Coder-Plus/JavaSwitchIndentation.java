package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Contents of switch block must be indented +2 relative to the switch statement. Each switch label (case, default) must start at that indentation level.
 * 规则ID: java-switch-indentation | 严重级别: WARNING | PMD优先级: 3
 * DISABLED: per evaluate2.txt, 0% precision (17 FP, 0 TP) - style preference, not a real violation
 */
public class JavaSwitchIndentation extends PMDBaseRule {
    @Override public Object visit(ASTSwitchStatement node, Object data) {
        return data;
    }
}