/**
 * CodeQL 查询: javascript-generic-comma-line-wrapping
 * 描述: A comma must stay attached to the token that precedes it when line-wrapping.
 * 严重级别: WARNING
 *
 * 语言: javascript
 */

import javascript

from CallExpr ce, Expr arg1, Expr arg2
where
    arg1 = ce.getAnArgument() and
    arg2 = ce.getAnArgument() and
    arg1.getLocation().getEndLine() < arg2.getLocation().getStartLine() and
    arg1.getLocation().getEndColumn() + 1 != arg2.getLocation().getStartColumn()
select ce, "Comma must stay attached to the token that precedes it when line-wrapping."