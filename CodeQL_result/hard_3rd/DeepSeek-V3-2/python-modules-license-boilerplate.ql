/**
 * CodeQL 查询: python-modules-license-boilerplate
 * 描述: Every file should contain license boilerplate appropriate for the project and start with a docstring describing contents and usage.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Module m
where not exists(m.getADocString())
  and m.getFile().getBaseName().regexpMatch(".+\\.py")
select m, m.getFile().getBaseName(), "Every file should contain license boilerplate appropriate for the project and start with a docstring describing contents and usage."