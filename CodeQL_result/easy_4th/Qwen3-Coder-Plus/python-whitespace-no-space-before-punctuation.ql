import python

from Call c, Argument a
where
    a.getArgIndex() = 0
    and a.getParent() = c
    and c.getAnArg().getLocation().getStartColumn() > c.getLocation().getStartColumn() + 2
    and c.toString().regexpMatch(".*, .*")
select c, "No whitespace before a comma, semicolon, or colon."