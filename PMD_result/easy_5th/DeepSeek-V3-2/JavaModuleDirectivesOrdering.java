package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Wildcard (on-demand) imports, static or otherwise, are not used.
 * 规则ID: java-no-wildcard-import | 严重级别: WARNING | PMD优先级: 3
 * 检测Java文件中的通配符导入语句(import xxx.*)
 */
public class JavaModuleDirectivesOrdering extends PMDBaseRule {
    public JavaModuleDirectivesOrdering() { setMessage("Wildcard (on-demand) imports, static or otherwise, are not used."); }
    @Override public Object visit(ASTImportDeclaration node, Object data) {
        if (node.isStatic()) {
            ASTName nameNode = node.getFirstChildOfType(ASTName.class);
            if (nameNode != null && nameNode.getImage() != null && nameNode.getImage().endsWith(".*")) {
                addViolation(data, node);
            }
        } else {
            if (node.isOnDemand()) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }
}