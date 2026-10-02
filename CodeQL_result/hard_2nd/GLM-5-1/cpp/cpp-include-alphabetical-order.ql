import cpp

from Include i1, Include i2
where
    i1.getFile() = i2.getFile() and
    i1.getLocation().getEndLine() + 1 = i2.getLocation().getStartLine() and
    i1.getIncludeText().toString() > i2.getIncludeText().toString()
select i2, "Include not in alphabetical order: " + i2.getIncludeText().toString() + " should come before " + i1.getIncludeText().toString()
