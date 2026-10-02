package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class JavaModuleDirectivesOrdering extends PMDBaseRule {
    public JavaModuleDirectivesOrdering() { setMessage("Module directives must be ordered in the sequence: requires block, exports block, opens block, uses block, provides block."); }
    @Override public Object visit(ASTCompilationUnit node, Object data) {
        return super.visit(node, data);
    }
}