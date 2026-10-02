import python

from StringLiteral s1, StringLiteral s2
where
    s1.getFile() = s2.getFile() and
    s1 != s2 and
    s1.getRawValue().regexpMatch("^\"") and
    s2.getRawValue().regexpMatch("^'")
select s1, "Inconsistent string quote character in file."
