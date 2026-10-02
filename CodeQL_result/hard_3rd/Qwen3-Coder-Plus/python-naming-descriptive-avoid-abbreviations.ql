import python

from Function f
where
    f.getName().regexpMatch("[a-z].*") and
    not f.getName().regexpMatch("test.*") and
    not f.getName().regexpMatch("__.*") and
    f.getName().length() <= 3
select f, "Names should be descriptive. Avoid abbreviations that are ambiguous or unfamiliar: " + f.getName()