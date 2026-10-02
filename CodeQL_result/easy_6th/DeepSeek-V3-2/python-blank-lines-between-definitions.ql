/**
 * CodeQL 查询: python-blank-lines-between-definitions
 * 描述: Require exactly two blank lines between top-level function or class definitions.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Function f1, Function f2
where
    f1.getEnclosingModule() = f2.getEnclosingModule()
    and f1.getLocation().getEndLine() + 3 > f2.getLocation().getStartLine()
    and f1.getLocation().getEndLine() < f2.getLocation().getStartLine()
    and not f2.getName().matches("__init__%")
select f2, "Require exactly two blank lines between top-level function or class definitions."