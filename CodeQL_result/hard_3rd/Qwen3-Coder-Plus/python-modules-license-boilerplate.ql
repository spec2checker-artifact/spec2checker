import python

from Module m
where
    not exists(StringLiteral sl | sl.getFile() = m.getFile() and sl.getText().regexpMatch("(?i).*license.*"))
select m, "Every file should contain license boilerplate appropriate for the project."