package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Module directives must be ordered in the sequence: requires block, exports block, opens block, uses block, provides block.
 * 规则ID: java-module-directives-ordering | 严重级别: WARNING | PMD优先级: 3
 */
public class JavaModuleDirectivesOrdering extends PMDBaseRule {
    public JavaModuleDirectivesOrdering() { setMessage("Module directives must be ordered in the sequence: requires block, exports block, opens block, uses block, provides block. Each block must be separated by a single blank line."); }
    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        String source = node.getSourceCode();
        if (source != null && source.contains("module ")) {
            int lastRequires = -1, lastExports = -1, lastOpens = -1, lastUses = -1, lastProvides = -1;
            String[] lines = source.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.startsWith("requires ") || line.startsWith("requires static ") || line.startsWith("requires transitive ")) {
                    lastRequires = i;
                } else if (line.startsWith("exports ")) {
                    lastExports = i;
                } else if (line.startsWith("opens ")) {
                    lastOpens = i;
                } else if (line.startsWith("uses ")) {
                    lastUses = i;
                } else if (line.startsWith("provides ")) {
                    lastProvides = i;
                }
            }
            if (lastExports != -1 && lastRequires != -1 && lastExports < lastRequires) {
                addViolation(data, node);
            } else if (lastOpens != -1 && lastExports != -1 && lastOpens < lastExports) {
                addViolation(data, node);
            } else if (lastUses != -1 && lastOpens != -1 && lastUses < lastOpens) {
                addViolation(data, node);
            } else if (lastProvides != -1 && lastUses != -1 && lastProvides < lastUses) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }
}