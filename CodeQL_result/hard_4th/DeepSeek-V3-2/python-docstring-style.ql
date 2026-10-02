/**
 * CodeQL 查询: python-docstring-style
 * 描述: Module, function, and method docstrings must use triple double quotes. Summary line must be ≤80 characters, followed by a blank line if more content exists.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Function f, StringLiteral doc
where
    doc = f.getDocString()
    and not f.getName().regexpMatch("^__")
    and not f.getName().regexpMatch("^test_")
    and not f.isLambda()
    and doc.getValue().length() > 80
select f, "Module, function, and method docstrings must use triple double quotes. Summary line must be ≤80 characters, followed by a blank line if more content exists."