import cpp

from Include i1, Include i2
where
    i1.getFile() = i2.getFile() and
    i1.getLocation().getStartLine() < i2.getLocation().getStartLine() and
    i2.getLocation().getStartLine() - i1.getLocation().getStartLine() <= 5 and
    i1.toString() > i2.toString()
select i1, "Includes not in alphabetical order: " + i1.toString() + " should come after " + i2.toString()