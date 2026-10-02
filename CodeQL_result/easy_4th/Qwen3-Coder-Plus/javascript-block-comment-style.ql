import javascript

from Comment c
where
    c.toString().regexpMatch("(?s)/\\*.*\\*/")
    and not c.toString().matches("/* *")
    and not c.toString().regexpMatch("/\\*\\*[ \\t]*\\*/")
select c, "Block comments must be indented at the same level as surrounding code and use /* ... */ style."