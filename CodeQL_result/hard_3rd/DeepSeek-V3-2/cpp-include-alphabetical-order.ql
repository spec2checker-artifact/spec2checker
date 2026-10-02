/**
 * CodeQL 查询: cpp-include-alphabetical-order
 * 描述: Within each include section, includes should be ordered alphabetically.
 * 严重级别: WARNING
 *
 * 语言: cpp
 */

import cpp

from IncludeDirective i1, IncludeDirective i2
where i1.getFile() = i2.getFile()
  and i1.getLocation().getStartLine() + 1 = i2.getLocation().getStartLine()
  and i1.isLocalInclude() = i2.isLocalInclude()
  and i1.getName() > i2.getName()
select i1, "Within each include section, '" + i1.getName() + "' should come after '" + i2.getName() + "'. Includes should be ordered alphabetically."