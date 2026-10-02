import python

from ExprStmt s
 where s.getLocation().getStartColumn() % 4 != 0
 and s.getLocation().getStartColumn() > 1
  and not s.getLocation().getStartColumn() = 1
select s, "Indent code blocks with 4 spaces. Never use tabs. Found inconsistent indentation."