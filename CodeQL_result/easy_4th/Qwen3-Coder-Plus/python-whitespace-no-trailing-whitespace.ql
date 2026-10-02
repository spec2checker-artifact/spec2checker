import python

from Expr e, Line l
where
    l.getNum() = e.getLocation().getStartLine()
    and l.getText().regexpMatch(".*[ \\t]+$")
    and e.toString().regexpMatch(".*")
select e, "Detect lines that end with whitespace characters."