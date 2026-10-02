/**
 * CodeQL 查询: javascript-line-wrapping-comma-preceding-token
 * 描述: A comma must stay attached to the token that precedes it when line-wrapping.
 * 严重级别: WARNING
 *
 * 语言: javascript
 */

import javascript

from ArrayExpr ae
where
    ae.getNumElements() > 1 and
    ae.getElement(0).getEndLine() < ae.getElement(1).getBeginLine()
select ae, "A comma must stay attached to the token that precedes it when line-wrapping."
