import python

from Call c, Name arg
where
    arg = c.getAnArgument()
    and arg.getLocation().getStartColumn() - c.getLocation().getStartColumn() > 2
    and c.getLocation().getStartColumn() > 1
select c, "No whitespace inside parentheses; argument starts more than 2 columns after opening paren."