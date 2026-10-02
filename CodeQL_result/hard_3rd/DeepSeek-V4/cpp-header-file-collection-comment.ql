import cpp

from File f, Class c
where
    f.getExtension().regexpMatch("h|hpp|hxx") and
    c.getFile() = f and
    exists(Class other | other.getFile() = f | other != c)
select c, "Header file declares multiple user-facing abstractions. It must include a comment describing the collection."
