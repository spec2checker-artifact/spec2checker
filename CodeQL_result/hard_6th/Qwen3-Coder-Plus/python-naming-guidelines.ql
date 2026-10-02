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
    f.getName() != "__init__"
    and not f.getName().matches("test%")
select f, "Enforce naming conventions for Python identifiers: packages `lower_with_under`, classes `CapWords`, exceptions `CapWords`, functions `lower_with_under()`, constants `CAPS_WITH_UNDER`. Internal versions prefix `_`."
