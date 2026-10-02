/**
 * CodeQL 查询: python-comment-indentation
 * 描述: Block and inline comments should be used for tricky, non-obvious, interesting, or important parts of code and should start at least 2 spaces from the code.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Function f
where
    not exists(f.getDocString())
    and not f.getName().regexpMatch("^__")
    and not f.getName().regexpMatch("^test_")
    and not f.getName().regexpMatch("^_")
    and not f.isLambda()
select f, "Block and inline comments should be used for tricky, non-obvious, interesting, or important parts of code and should start at least 2 spaces from the code."