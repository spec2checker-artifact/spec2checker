import cpp

from IncludeDirective i1, IncludeDirective i2
where
    i1.getFile() = i2.getFile() and
    i1.getLocation().getStartLine() + 1 = i2.getLocation().getStartLine() and
    i1.toString() > i2.toString()
select i2, "Include not in alphabetical order: " + i2.toString() + " should come before " + i1.toString()