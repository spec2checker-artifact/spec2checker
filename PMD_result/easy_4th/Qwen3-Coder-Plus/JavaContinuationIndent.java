package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class JavaContinuationIndent extends PMDBaseRule {
    public JavaContinuationIndent() { setMessage("Each continuation line must be indented at least +4 spaces from the original line."); }
    @Override public Object visit(ASTCompilationUnit node, Object data) {
        return super.visit(node, data);
    }
}