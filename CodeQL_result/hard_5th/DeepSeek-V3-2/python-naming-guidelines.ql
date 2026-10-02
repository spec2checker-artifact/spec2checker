import python

from Class c
where c.getName().regexpMatch("^[a-z]")
  and c.getName() != ""
select c, "Class '" + c.getName() + "' should use CapWords (PascalCase) naming convention."