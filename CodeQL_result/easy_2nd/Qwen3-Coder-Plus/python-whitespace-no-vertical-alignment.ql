import python

from Assign a1, Assign a2
where
    a1.getTarget().(Name).getId() != a2.getTarget().(Name).getId()
    and a1.getLocation().getStartLine() + 1 = a2.getLocation().getStartLine()
    and a1.getTarget().getLocation().getStartColumn() = a2.getTarget().getLocation().getStartColumn()
    and a1.getTarget().getLocation().getStartColumn() > 4
    and a1.getTarget().getLocation().getStartColumn() != 1
select a1, "Don't use spaces to vertically align tokens on consecutive lines; it becomes a maintenance burden."