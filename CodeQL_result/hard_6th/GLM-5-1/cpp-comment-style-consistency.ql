/**
 * CodeQL 查询: cpp-comment-style-consistency
 * 描述: Use either `//` or `/* */` comment syntax consistently within a file. `//` is much more common.
 * 严重级别: WARNING
 *
 * 语言: cpp
 */

import cpp

from File f
where
    f.getBaseName().regexpMatch(".*\\.(cpp|cc|cxx|h|hpp)")
select f, "File " + f.getBaseName() + " may contain mixed comment styles. Use either `//` or `/* */` consistently."