/**
 * CodeQL 查询: python-whitespace-no-vertical-alignment
 * 描述: Don't use spaces to vertically align tokens on consecutive lines; it becomes a maintenance burden.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from AssignStmt a
where
    a.getLocation().getStartColumn() > 8
select a, "Assignment appears to be vertically aligned with extra spaces; this is a maintenance burden."
