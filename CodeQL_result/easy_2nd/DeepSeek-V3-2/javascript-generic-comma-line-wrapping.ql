import javascript

from CallExpr ce, Argument a1, Argument a2
where
    ce.getNumArgument() > 1
    and a1 = ce.getAnArgument()
    and a2 = ce.getAnArgument()
    and a1 != a2
    and a1.getLocation().getEndLine() != a2.getLocation().getStartLine()
    and a1.getLocation().getEndColumn() > 1
select ce, "A comma must stay attached to the token that precedes it when line-wrapping; argument on a different line suggests line-wrapped comma."