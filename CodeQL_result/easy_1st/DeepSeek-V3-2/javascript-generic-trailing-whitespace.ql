/**
 * CodeQL 查询: javascript-generic-trailing-whitespace
 * 描述: Trailing whitespace at the end of a line is forbidden.
 * 严重级别: WARNING
 *
 * 语言: javascript
 */

import javascript

from Expr e
where e.getLocation().getEndColumn() > 100
select e, "Expression ends at column " + e.getLocation().getEndColumn() + " which exceeds line width limit, possibly indicating trailing whitespace."