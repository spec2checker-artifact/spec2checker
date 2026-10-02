import python

from Function f
where
    f.getLocation().getEndLine() - f.getLocation().getStartLine() > 50 and
    not f.getName().regexpMatch("__[a-zA-Z]+__")
select f, "Function exceeds 50 lines which may indicate missing or inconsistent comments."
