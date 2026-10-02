import cpp

from File f
where
    f.getExtension().regexpMatch("(h|cc|cpp|cxx|c|hpp|hxx|inc)")
    and not f.getBaseName().regexpMatch("[a-z0-9._\\-]+\\.(h|cc|cpp|cxx|c|hpp|hxx|inc)")
select f, "Filename '" + f.getBaseName() + "' should be all lowercase with underscores or dashes only."