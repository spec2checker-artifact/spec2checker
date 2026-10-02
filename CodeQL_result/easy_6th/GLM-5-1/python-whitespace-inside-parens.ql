import python

from Call c
where
    c.getNumArgs() > 0
    and c.getArg(0).getLocation().getStartColumn() - c.getLocation().getStartColumn() > 2
select c, "Whitespace detected inside parentheses after call."