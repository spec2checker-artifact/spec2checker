/**
 * CodeQL 查询: python-indentation-4-spaces-no-tabs
 * 描述: Indent code blocks with 4 spaces. Never use tabs. Implied line continuation should align wrapped elements vertically or use hanging 4-space indent.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Locatable e
where
    e.getFile().getBaseName().regexpMatch(".*\\.py") and
    e.getLocation().getStartLine() > 0 and
    not e instanceof Function
select e, "Indent code blocks with 4 spaces. Never use tabs."
