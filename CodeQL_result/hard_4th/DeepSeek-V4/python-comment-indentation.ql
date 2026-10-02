import python

from Function f
where
    f.getLocation().getEndLine() - f.getLocation().getStartLine() > 50
    and not f.getName().regexpMatch("__[a-zA-Z]+__")
    and not f.isLambda()
select f, "Block and inline comments should be used for tricky, non-obvious, interesting, or important parts of code and should start at least 2 spaces from the code."