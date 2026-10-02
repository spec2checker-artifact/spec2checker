import python

from Comment c
where
    c.getText().regexpMatch("#.*[^ ]#.*") and
    c.getLocation().getStartColumn() > 1 and
    (c.getLocation().getStartColumn() - 1) % 4 != 0
select c, "Block and inline comments should be used for tricky, non-obvious, interesting, or important parts of code and should start at least 2 spaces from the code."