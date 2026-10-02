/**
 * CodeQL 查询: javascript-generic-trailing-whitespace
 * 描述: Trailing whitespace at the end of a line is forbidden.
 * 严重级别: WARNING
 *
 * 语言: javascript
 */

import javascript

from Expr e
where
    e.getEndColumn() > 80 and
    e.getFile().getShortName().regexpMatch(".*\\.js$")
select e, "Trailing whitespace at the end of a line is forbidden."