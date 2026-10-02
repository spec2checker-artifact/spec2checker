/**
 * CodeQL 查询: python-whitespace-no-space-before-punctuation
 * 描述: No whitespace before a comma, semicolon, or colon. Use space after them.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Call c, Expr arg1, Expr arg2
where
    arg1 = c.getAnArgument() and
    arg2 = c.getAnArgument() and
    arg2.getLocation().getStartLine() = arg1.getLocation().getEndLine() and
    arg2.getLocation().getStartColumn() > arg1.getLocation().getEndColumn() + 2
select c, "Extra whitespace before punctuation in call arguments."