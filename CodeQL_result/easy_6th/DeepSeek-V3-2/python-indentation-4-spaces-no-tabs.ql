/**
 * CodeQL 查询: python-indentation-4-spaces-no-tabs
 * 描述: Indent code blocks with 4 spaces. Never use tabs. Implied line continuation should align wrapped elements vertically or use hanging 4-space indent.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Function f, Parameter p
where
    p.getFunction() = f
    and p.getName().regexpMatch(".*\\t.*")
select f, "Indent code blocks with 4 spaces. Never use tabs. Found tab character in function parameter."