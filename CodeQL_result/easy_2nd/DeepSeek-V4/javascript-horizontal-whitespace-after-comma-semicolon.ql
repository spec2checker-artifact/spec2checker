import javascript

from ExprStmt es
where
    exists(Expr e |
        e = es.getExpr()
        and e.getLocation().getEndColumn() - e.getLocation().getStartColumn() > 80
    )
select es, "Expression line exceeds 80 columns; may indicate missing whitespace after comma or semicolon."