/**
 * CodeQL 查询: python-indentation-4-spaces-no-tabs
 * 描述: Indent code blocks with 4 spaces. Never use tabs. Implied line continuation should align wrapped elements vertically or use hanging 4-space indent.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Stmt s
where
    s.getBeginColumn() > 1 and
    s.getBeginColumn() % 4 != 1
select s, "Indent code blocks with 4 spaces. Never use tabs."