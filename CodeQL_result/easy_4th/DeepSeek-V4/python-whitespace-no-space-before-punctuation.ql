/**
 * CodeQL 查询: python-whitespace-no-space-before-punctuation
 * 描述: No whitespace before a comma, semicolon, or colon. Use space after them.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Call c
where
    c.getAnArg().getLocation().getStartColumn() > c.getLocation().getStartColumn() + 2
select c, "No whitespace before a comma, semicolon, or colon."
