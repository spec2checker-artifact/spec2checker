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
    e.getLocation().getEndColumn() > 80
select e, "Line exceeds column limit which may indicate trailing whitespace."
