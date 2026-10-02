import python

from Function f
where
    exists(f.getDocString())
    and not f.getName().regexpMatch("__[a-zA-Z]+__")
    and not f.isLambda()
    and f.getDocString().getValue().length() > 80
select f, "Module, function, and method docstrings must use triple double quotes. Summary line must be ≤80 characters, followed by a blank line if more content exists."