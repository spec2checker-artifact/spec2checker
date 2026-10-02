/**
 * CodeQL 查询: python-whitespace-no-vertical-alignment
 * 描述: Don't use spaces to vertically align tokens on consecutive lines; it becomes a maintenance burden.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Assignment a1, Assignment a2
where
    a1.getLocation().getStartLine() + 1 = a2.getLocation().getStartLine() and
    a1.getLocation().getStartColumn() != a2.getLocation().getStartColumn() and
    a1.getTarget().getLocation().getStartColumn() != a2.getTarget().getLocation().getStartColumn() and
    abs(a1.getTarget().getLocation().getStartColumn() - a2.getTarget().getLocation().getStartColumn()) > 4
select a2, "Vertically aligned assignments on consecutive lines create maintenance burden."