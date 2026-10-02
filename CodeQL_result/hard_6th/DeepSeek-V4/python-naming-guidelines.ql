/**
 * CodeQL 查询: python-naming-guidelines
 * 描述: Enforce naming conventions for Python identifiers: packages `lower_with_under`, classes `CapWords`, exceptions `CapWords`, functions `lower_with_under()`, constants `CAPS_WITH_UNDER`. Internal versions prefix `_`.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Class c
where
    not c.getName().matches("%Test%") and
    not c.getName().matches("%Error%") and
    not c.getName().matches("%Exception%") and
    not c.getName().regexpMatch("[A-Z][a-zA-Z0-9]*") and
    not c.getName().regexpMatch("_[a-z].*")
select c, "Enforce naming conventions for Python identifiers: packages `lower_with_under`, classes `CapWords`, exceptions `CapWords`, functions `lower_with_under()`, constants `CAPS_WITH_UNDER`. Internal versions prefix `_`."