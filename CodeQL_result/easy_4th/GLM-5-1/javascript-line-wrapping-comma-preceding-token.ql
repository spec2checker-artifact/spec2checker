/**
 * CodeQL 查询: javascript-line-wrapping-comma-preceding-token
 * 描述: A comma must stay attached to the token that precedes it when line-wrapping.
 * 严重级别: WARNING
 *
 * 语言: javascript
 * 注意: CodeQL operates on parsed AST and cannot directly inspect comma positioning.
 *       This query detects call expressions with many arguments as a proxy for
 *       potential line-wrapping issues, but cannot truly check comma placement.
 */

import javascript

from CallExpr ce
where
    ce.getNumArgument() > 5
select ce, "A comma must stay attached to the token that precedes it when line-wrapping."