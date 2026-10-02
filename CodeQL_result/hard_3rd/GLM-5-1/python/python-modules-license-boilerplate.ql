import python

from Module m
where
    m.getFile().getBaseName() != "__init__" and
    m.getFile().getBaseName() != "conftest" and
    not exists(ExprStmt es |
        es.getEnclosingModule() = m and
        es.getLocation().getStartLine() <= 5 and
        es.getExpr() instanceof StringLiteral
    )
select m, "Module missing license boilerplate or docstring at beginning."
