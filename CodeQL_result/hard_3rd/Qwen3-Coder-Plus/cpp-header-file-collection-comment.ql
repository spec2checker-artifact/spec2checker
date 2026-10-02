import cpp

from File f
where
    f.getBaseName().regexpMatch(".*\\.h$") and
    not exists(Include i | i.getFile() = f)
select f, "If a header file declares multiple user-facing abstractions, it must include a comment describing the collection."