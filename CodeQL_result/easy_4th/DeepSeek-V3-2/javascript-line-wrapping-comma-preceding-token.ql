/**
 * CodeQL 查询: javascript-line-wrapping-comma-preceding-token
 * 描述: A comma must stay attached to the token that precedes it when line-wrapping.
 * 严重级别: WARNING
 *
 * 语言: javascript
 */

import javascript

from CallExpr ce
where
    ce.getCallee().(VarAccess).getName() = "unknown"
select ce, "A comma must stay attached to the token that precedes it when line-wrapping."
