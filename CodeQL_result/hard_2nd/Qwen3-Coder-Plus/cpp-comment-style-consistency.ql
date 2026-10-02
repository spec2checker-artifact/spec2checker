import cpp

from Comment c1, Comment c2, File f
where
    c1.getFile() = f and
    c2.getFile() = f and
    c1.getStyle() != c2.getStyle() and
    f.getBaseName().regexpMatch(".*\\.(h|hpp|hh|cc|cpp|cxx|c)$")
select f, "File uses mixed comment styles: " + c1.getStyle() + " and " + c2.getStyle()