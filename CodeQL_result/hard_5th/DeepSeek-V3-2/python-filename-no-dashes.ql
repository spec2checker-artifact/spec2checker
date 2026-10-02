import python

from Module m
where m.getFile().getShortName().regexpMatch(".*-.*\\.py")
select m, "Python file '" + m.getFile().getShortName() + "' must not contain dashes (-)."