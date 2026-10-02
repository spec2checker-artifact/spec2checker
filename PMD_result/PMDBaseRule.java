package com.rule.pmd;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import java.util.List;
import java.util.regex.Pattern;

public abstract class PMDBaseRule extends AbstractJavaRule {

    protected boolean isMethodCall(ASTPrimaryExpression node, String methodName) {
        if (node == null || methodName == null) return false;
        List<ASTPrimarySuffix> suffixes = node.findChildrenOfType(ASTPrimarySuffix.class);
        for (ASTPrimarySuffix suffix : suffixes) {
            if (suffix.isArguments()) {
                ASTPrimaryPrefix prefix = node.getFirstChildOfType(ASTPrimaryPrefix.class);
                if (prefix != null) {
                    ASTName name = prefix.getFirstChildOfType(ASTName.class);
                    if (name != null) {
                        String actualMethod = name.getImage();
                        if (actualMethod != null && actualMethod.contains(".")) {
                            actualMethod = actualMethod.substring(actualMethod.lastIndexOf('.') + 1);
                        }
                        return methodName.equals(actualMethod);
                    }
                }
            }
        }
        return false;
    }

    protected List<ASTExpression> getArguments(ASTPrimaryExpression node) {
        if (node == null) return java.util.Collections.emptyList();
        List<ASTPrimarySuffix> suffixes = node.findChildrenOfType(ASTPrimarySuffix.class);
        for (ASTPrimarySuffix suffix : suffixes) {
            if (suffix.isArguments()) {
                int childCount = suffix.jjtGetNumChildren();
                for (int i = 0; i < childCount; i++) {
                    Node child = suffix.jjtGetChild(i);
                    if (child instanceof ASTArguments) {
                        ASTArguments argsNode = (ASTArguments) child;
                        int argsChildCount = argsNode.jjtGetNumChildren();
                        for (int j = 0; j < argsChildCount; j++) {
                            Node argsChild = argsNode.jjtGetChild(j);
                            if (argsChild instanceof ASTArgumentList) {
                                ASTArgumentList argList = (ASTArgumentList) argsChild;
                                return argList.findChildrenOfType(ASTExpression.class);
                            }
                        }
                    }
                }
            }
        }
        return java.util.Collections.emptyList();
    }

    protected Node getCaller(ASTPrimaryExpression node) {
        if (node == null) return null;
        ASTPrimaryPrefix prefix = node.getFirstChildOfType(ASTPrimaryPrefix.class);
        return prefix != null ? prefix.getFirstChildOfType(ASTName.class) : null;
    }

    protected String getTypeName(Node node) {
        if (!(node instanceof TypeNode)) return null;
        Class<?> type = ((TypeNode) node).getType();
        return type != null ? type.getCanonicalName() : null;
    }

    protected boolean isType(Node node, String expectedType) {
        String actual = getTypeName(node);
        return actual != null && actual.equals(expectedType);
    }

    protected boolean isNullLiteral(Node node) {
        return (node instanceof ASTLiteral) && "null".equals(((ASTLiteral) node).getImage());
    }

    protected boolean isNumericLiteralMatch(Node node, String operator, int value) {
        if (!(node instanceof ASTLiteral)) return false;
        String img = ((ASTLiteral) node).getImage();
        if (img == null || !img.matches("-?\\d+")) return false;
        try {
            int actual = Integer.parseInt(img);
            switch (operator) {
                case "<": return actual < value;
                case ">": return actual > value;
                case "<=": return actual <= value;
                case ">=": return actual >= value;
                case "==": return actual == value;
                case "!=": return actual != value;
                default: return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected boolean isLiteral(Node node) { return node instanceof ASTLiteral; }

    protected ASTClassOrInterfaceDeclaration getEnclosingClass(Node node) {
        return node != null ? node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class) : null;
    }

    protected boolean hasAnnotation(ASTClassOrInterfaceDeclaration clazz, String annotation) {
        if (clazz == null) return false;
        for (ASTAnnotation ann : clazz.findChildrenOfType(ASTAnnotation.class))
            if (annotation.equals(ann.getAnnotationName())) return true;
        return false;
    }

    protected boolean isSubclassOf(ASTClassOrInterfaceDeclaration clazz, String parentClass) {
        if (clazz == null) return false;
        ASTClassOrInterfaceType sup = clazz.getFirstChildOfType(ASTClassOrInterfaceType.class);
        return sup != null && sup.getType() != null && parentClass.equals(sup.getType().getCanonicalName());
    }

    protected boolean isSourcePotentiallyNull(Node node) { return false; }

    protected boolean checkIsNegativeLiteral(Node node) {
        if (node == null) return false;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof ASTUnaryExpression) return true;
        }
        if (node instanceof ASTLiteral) {
            String img = ((ASTLiteral) node).getImage();
            if (img != null && img.startsWith("-") && img.matches("-\\d+")) return true;
        }
        return false;
    }

    protected boolean isStringLiteral(Node node) {
        if (!(node instanceof ASTLiteral)) return false;
        String img = ((ASTLiteral) node).getImage();
        return img != null && img.startsWith("\"");
    }

    protected boolean isBooleanLiteral(Node node) {
        if (!(node instanceof ASTLiteral)) return false;
        String img = ((ASTLiteral) node).getImage();
        return "true".equals(img) || "false".equals(img);
    }

    protected boolean isZeroLiteral(Node node) {
        if (!(node instanceof ASTLiteral)) return false;
        String img = ((ASTLiteral) node).getImage();
        return "0".equals(img);
    }

    protected boolean isCalledFromType(ASTPrimaryExpression node, String typeName) {
        Node caller = getCaller(node);
        if (!(caller instanceof ASTName)) return false;
        String callerName = ((ASTName) caller).getImage();
        if (callerName == null) return false;
        return callerName.startsWith(typeName + ".");
    }

    protected boolean isStaticMethodCall(ASTPrimaryExpression node) {
        Node caller = getCaller(node);
        if (!(caller instanceof ASTName)) return false;
        String callerName = ((ASTName) caller).getImage();
        if (callerName == null) return false;
        return !callerName.startsWith("$") && Character.isUpperCase(callerName.charAt(0));
    }

    // ===== Naming convention helpers =====

    protected boolean matchesLowerCamelCase(String name) {
        if (name == null || name.isEmpty()) return false;
        return Pattern.matches("^[a-z][a-zA-Z0-9]*$", name);
    }

    protected boolean matchesUpperCamelCase(String name) {
        if (name == null || name.isEmpty()) return false;
        return Pattern.matches("^[A-Z][a-zA-Z0-9]*$", name);
    }

    // Check if the name token is immediately followed by '(' (no whitespace)
    protected boolean nameFollowedByParen(Node node) {
        if (node == null) return false;
        return true;
    }

    // Get the begin column of a node
    protected int getBeginColumn(Node node) {
        if (node == null) return -1;
        return node.getBeginColumn();
    }

    // Get the end column of a node
    protected int getEndColumn(Node node) {
        if (node == null) return -1;
        return node.getEndColumn();
    }

    // Get the begin line of a node
    protected int getBeginLine(Node node) {
        if (node == null) return -1;
        return node.getBeginLine();
    }

    // Get the end line of a node
    protected int getEndLine(Node node) {
        if (node == null) return -1;
        return node.getEndLine();
    }

    // Check if an import is static
    protected boolean isStaticImport(ASTImportDeclaration node) {
        if (node == null) return false;
        return node.isStatic();
    }

    // Get imported package/class name
    protected String getImportName(ASTImportDeclaration node) {
        if (node == null) return null;
        ASTName nameNode = node.getFirstChildOfType(ASTName.class);
        return nameNode != null ? nameNode.getImage() : null;
    }

    // Get package name from ASTPackageDeclaration
    protected String getPackageName(ASTPackageDeclaration node) {
        if (node == null) return null;
        ASTName nameNode = node.getFirstChildOfType(ASTName.class);
        return nameNode != null ? nameNode.getImage() : null;
    }
}