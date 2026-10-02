import python

from Module m
where
    not exists(StrConst s | s = m.getDocString() and s.getText().regexpMatch("(?is).*license.*|.*copyright.*|.*spdx.*"))
select m, "Python module missing license boilerplate in docstring"