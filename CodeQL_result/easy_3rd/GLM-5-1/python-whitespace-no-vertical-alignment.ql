/**
 * CodeQL 查询: python-whitespace-no-vertical-alignment
 * 描述: Don't use spaces to vertically align tokens on consecutive lines; it becomes a maintenance burden.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Assign a
where a.getFile().getRelativePath().regexpMatch(".*\\.py$")
  and a.getTarget().(Name).getId().regexpMatch("[a-z_][a-zA-Z0-9_]*")
  and exists(Assign b |
    b.getFile() = a.getFile()
    and b.getLocation().getStartLine() = a.getLocation().getStartLine() + 1
    and b.getTarget().(Name).getId() = a.getTarget().(Name).getId()
    and b.getLocation().getStartColumn() != a.getLocation().getStartColumn()
  )
select a, "Don't use spaces to vertically align tokens on consecutive lines; it becomes a maintenance burden."