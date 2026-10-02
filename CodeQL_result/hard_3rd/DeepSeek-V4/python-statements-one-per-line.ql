import python

from ExprStmt s1, ExprStmt s2
where
    s1.getFile() = s2.getFile() and
    s1.getLocation().getStartLine() = s2.getLocation().getStartLine() and
    s1.getLocation().getStartColumn() < s2.getLocation().getStartColumn()
select s1, "Multiple statements on the same line."
