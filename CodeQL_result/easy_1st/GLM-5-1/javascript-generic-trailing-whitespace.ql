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
    e.toString().regexpMatch(".*[ \\t]+$")
select e, "Trailing whitespace detected."
