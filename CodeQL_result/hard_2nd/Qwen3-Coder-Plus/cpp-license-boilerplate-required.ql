import cpp

from File f
where
    f.getBaseName().regexpMatch(".*\\.(h|hpp|hh|cc|cpp|cxx|c)$") and
    not exists(Comment c |
        c.getFile() = f and
        c.getLocation().getStartLine() <= 5
    )
select f, "File missing license boilerplate in first 5 lines."