import python

from Module m
where
    m.getFile().getBaseName() != "__init__" and
    m.getFile().getBaseName() != "conftest" and
    not exists(StringLiteral sl |
        sl.getEnclosingModule() = m and
        sl.getLocation().getStartLine() <= 5
    )
select m, "Python file missing docstring/license in first 5 lines."