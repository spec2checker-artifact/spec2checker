package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Even code that fits within column limit may be line-wrapped at author's discretion. Tip: extract method or local variable to avoid wrapping.
 * 规则ID: java-line-wrapping-discretionary | 严重级别: WARNING | PMD优先级: 3
 * DISABLED: per evaluate2.txt, 0% precision (40 FP, 0 TP) - rule is purely style preference, not a real violation
 */
public class JavaLineWrappingDiscretionary extends PMDBaseRule {
    @Override public Object visit(ASTBlock node, Object data) {
        return data;
    }
}