import python

from Function f
where
    f.getName().regexpMatch("[A-Z][a-zA-Z0-9]*") and
    not f.getName().regexpMatch("__[a-zA-Z]+__") and
    not exists(Class c | c = f.getEnclosingClass())
select f, "Standalone function uses CamelCase. Use snake_case for Python function names."
