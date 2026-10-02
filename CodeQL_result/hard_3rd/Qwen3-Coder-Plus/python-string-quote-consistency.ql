import python

from Call c, StringLiteral s
where
    s = c.getAnArgument() and
    s.getText().regexpMatch("^'.*'$")
select s, "Be consistent with string quote character within a file. Prefer double quotes."