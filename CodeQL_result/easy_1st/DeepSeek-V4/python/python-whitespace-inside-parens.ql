/**
 * CodeQL 查询: python-whitespace-inside-parens
 * 描述: No whitespace inside parentheses, brackets, or braces.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Call c
where
    c.getArg(0).getLocation().getStartColumn() - c.getLocation().getStartColumn() > 2
select c, "Whitespace detected inside parentheses after call."
