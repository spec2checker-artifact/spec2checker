import python

from Module m
where m.getFile().getBaseName().regexpMatch(".*-.*\\.py$")
select m, "Python filenames must not contain dashes (-)."