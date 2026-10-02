package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class JavaModuleDirectivesOrdering extends PMDBaseRule {
    public JavaModuleDirectivesOrdering() { setMessage("Module directives must be ordered as: all requires in one block, then exports, opens, uses, provides. Each block must be separated by a single blank line."); }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        return super.visit(node, data);
    }
}
