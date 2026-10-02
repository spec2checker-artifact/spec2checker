package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Module directives must be ordered in the sequence: requires block, exports block, opens block, uses block, provides block. Each block must be separated by a single blank line.
 * 规则ID: java-module-directives-ordering | 严重级别: WARNING | PMD优先级: 3
 * 注意：此格式化规则在 PMD 中难以精细实现，已禁用。
 */
public class JavaModuleDirectivesOrdering extends PMDBaseRule {
    public JavaModuleDirectivesOrdering() { setMessage("Module directives must be ordered in the sequence: requires block, exports block, opens block, uses block, provides block. Each block must be separated by a single blank line."); }
    @Override public Object visit(ASTCompilationUnit node, Object data) {
        // 因 PMD 无法可靠检测此格式，规则不报告问题
        return super.visit(node, data);
    }
}