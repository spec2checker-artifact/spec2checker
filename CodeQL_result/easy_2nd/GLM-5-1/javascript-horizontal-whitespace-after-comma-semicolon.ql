/**
 * CodeQL 查询: javascript-horizontal-whitespace-after-comma-semicolon
 * 描述: Space after comma (`,`) and semicolon (`;`); never before.
 * 严重级别: WARNING
 *
 * 语言: javascript
 */

import javascript

from CallExpr ce
where
    exists(Expr arg |
        arg = ce.getAnArgument() and
        arg.getLocation().getStartColumn() - 1 > arg.getPreviousArgument().getLocation().getEndColumn()
    )
select ce, "Missing space after comma in function call arguments."