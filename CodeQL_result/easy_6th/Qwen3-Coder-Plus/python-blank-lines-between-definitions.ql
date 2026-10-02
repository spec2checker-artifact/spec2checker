/**
 * CodeQL 查询: python-blank-lines-between-definitions
 * 描述: Require exactly two blank lines between top-level function or class definitions.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from ClassDef cls
where
    cls.getEnclosingStmt() = none and
    exists(ClassDef prev |
        prev.getEnclosingStmt() = none and
        prev.getEndLine() < cls.getBeginLine() and
        cls.getBeginLine() - prev.getEndLine() < 3
    )
select cls, "Require exactly two blank lines between top-level function or class definitions."