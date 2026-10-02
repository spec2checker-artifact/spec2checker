import python

from Comment c, Expr e
where
    c.getLocation().getStartColumn() > 1 and
    c.getLocation().getStartColumn() <= e.getLocation().getEndColumn() + 1 and
    e.getEnclosingStmt() = c.getEnclosingStmt() and
    c.getLocation().getStartLine() = e.getLocation().getEndLine()
select c, "Comment starts too close to code (less than 2 spaces separation)."
