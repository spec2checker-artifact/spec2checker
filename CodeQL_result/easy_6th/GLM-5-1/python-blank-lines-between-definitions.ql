import python

from Class c1, Class c2
where
    c1.getLocation().getFile() = c2.getLocation().getFile()
    and c1.getLocation().getEndLine() < c2.getLocation().getStartLine()
    and c2.getLocation().getStartLine() - c1.getLocation().getEndLine() < 3
    and not exists(Class c3 |
        c3.getLocation().getFile() = c1.getLocation().getFile() and
        c1.getLocation().getEndLine() < c3.getLocation().getStartLine() and
        c3.getLocation().getStartLine() < c2.getLocation().getStartLine()
    )
select c2, "Missing two blank lines before class definition '" + c2.getName() + "'."