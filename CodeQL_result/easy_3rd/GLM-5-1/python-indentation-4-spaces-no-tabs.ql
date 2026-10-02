/**
 * CodeQL 查询: python-indentation-4-spaces-no-tabs
 * 描述: Indent code blocks with 4 spaces. Never use tabs.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Function f
where f.getBody().getNumberOfLines() > 0
  and exists(Stmt s |
    s.getEnclosingFunction() = f
    and s.getLocation().getStartColumn() >= 1
    and s.getLocation().getStartColumn().toInt() % 4 != 0
    and not s.getLocation().getStartColumn().toInt() = 1
  )
select f, "Indent code blocks with 4 spaces. Never use tabs. Found inconsistent indentation."