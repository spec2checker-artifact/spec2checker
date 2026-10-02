package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.RuleContext;
import java.io.*;
import java.nio.file.*;

public class JavaVerticalWhitespaceBetweenMembers extends PMDBaseRule {
    public JavaVerticalWhitespaceBetweenMembers() { setMessage("A single blank line should appear between consecutive members or initializers of a class."); }
    @Override public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        return data;
    }
}