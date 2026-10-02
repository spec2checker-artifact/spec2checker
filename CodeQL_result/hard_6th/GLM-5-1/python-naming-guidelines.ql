/**
 * CodeQL 查询: python-naming-guidelines
 * 描述: Enforce naming conventions for Python identifiers: packages `lower_with_under`, classes `CapWords`, exceptions `CapWords`, functions `lower_with_under()`, constants `CAPS_WITH_UNDER`. Internal versions prefix `_`.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Function f
where
    not f.getName().regexpMatch("__.*__") and
    not f.getName().regexpMatch("test.*") and
    not f.getName().regexpMatch("_.*") and
    not f.getName().regexpMatch("[a-z][a-z0-9_]*") and
    not f.getName().regexpMatch("[A-Z][A-Za-z0-9]*")
select f, "Function '" + f.getName() + "' does not follow Python naming conventions."