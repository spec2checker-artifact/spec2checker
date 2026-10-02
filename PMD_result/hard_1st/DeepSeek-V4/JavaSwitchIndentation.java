package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Contents of switch block must be indented +2 relative to the switch statement. Each switch label (case, default) must start at that indentation level.
 * 规则ID: java-switch-indentation | 严重级别: WARNING | PMD优先级: 3
 * 注意：此风格规则过于严苛，已禁用。
 */
public class JavaSwitchIndentation extends PMDBaseRule {
    public JavaSwitchIndentation() { setMessage("Contents of switch block must be indented +2 relative to the switch statement. Each switch label (case, default) must start at that indentation level."); }
    @Override public Object visit(ASTCompilationUnit node, Object data) {
        return super.visit(node, data);
    }
}