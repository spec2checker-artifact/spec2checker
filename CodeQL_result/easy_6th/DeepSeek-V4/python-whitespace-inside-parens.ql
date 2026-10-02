/**
 * CodeQL 查询: python-whitespace-inside-parens
 * 描述: No whitespace inside parentheses, brackets, or braces.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Call c, Expr arg
where
    arg = c.getAnArg() and
    arg.getBeginColumn() > c.getBeginColumn() + 1 and
    arg.getBeginColumn() - c.getBeginColumn() > 2
select arg, "No whitespace inside parentheses."