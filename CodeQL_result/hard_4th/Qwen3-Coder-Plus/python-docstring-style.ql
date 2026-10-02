import python

from FunctionDefinition f
where
    f.getName() != "__init__"
    and not f.getName().matches("test%")
select f, "Module, function, and method docstrings must use triple double quotes. Summary line must be ≤80 characters, followed by a blank line if more content exists."