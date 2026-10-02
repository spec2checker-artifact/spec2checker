import python

from StringLiteral sl
where
    sl.getText().regexpMatch(".*\t.*")
select sl, "String literal contains tab character. Avoid tabs in source code."