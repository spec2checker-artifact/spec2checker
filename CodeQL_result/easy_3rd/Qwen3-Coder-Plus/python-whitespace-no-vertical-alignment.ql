import python

 from ExprStmt s1, ExprStmt s2
 where s1.getFile() = s2.getFile()
 and and s1.getLocation().getStartLine() = s2.getLocation().getStartLine() - 1
  and s1.getLocation().getStartColumn() > 0
  and s2.getLocation().getStartColumn() > 0
  and s1.getLocation().getStartColumn() > 0
select s1, "Don't use spaces to vertically align tokens on consecutive lines; it becomes a maintenance burden."