package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.RuleContext;
import java.io.*;
import java.nio.file.*;

public class JavaModuleDirectivesOrdering extends PMDBaseRule {
    public JavaModuleDirectivesOrdering() { setMessage("Module directives must be ordered: all requires in one block, then exports, opens, uses, provides. Each block must be separated by a single blank line."); }
    @Override public Object visit(ASTCompilationUnit node, Object data) {
        if (!(data instanceof RuleContext)) return super.visit(node, data);
        RuleContext ctx = (RuleContext) data;
        File sourceFile = ctx.getSourceCodeFile();
        if (sourceFile == null || !sourceFile.getName().equals("module-info.java")) {
            return super.visit(node, data);
        }
        try {
            String source = new String(Files.readAllBytes(sourceFile.toPath()));
            int requiresPos = source.indexOf("requires ");
);
            int exportsPos = source.indexOf("exports ");
            if (requiresPos > 0 && exportsPos > 0 && exportsPos < requiresPos) {
                addViolation(data, node);
            }
        } catch (Exception e) {}
        return super.visit(node, data);
    }
 }