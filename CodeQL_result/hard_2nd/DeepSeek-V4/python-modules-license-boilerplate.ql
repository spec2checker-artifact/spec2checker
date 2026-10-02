/**
 * CodeQL 查询: python-modules-license-boilerplate
 * 描述: Every Python file should contain a license boilerplate and a docstring at the beginning.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Module m
where
    not exists(PyStringLiteral s |
        s.getEnclosingModule() = m and
        s.getStartLine() <= 5
    )
select m, "Every Python file should contain a license boilerplate and a docstring at the beginning."