/**
 * CodeQL 查询: cpp-header-file-collection-comment
 * 描述: If a header file declares multiple user-facing abstractions, it must include a comment describing the collection.
 * 严重级别: WARNING
 *
 * 语言: cpp
 */

import cpp

from File f
where f.getBaseName().regexpMatch(".+\\.h$|.+\\.hpp$")
  and exists(Function f1 | f1.getFile() = f)
  and exists(Function f2 | f2.getFile() = f and f2 != f1)
select f, f.getBaseName(), "If a header file declares multiple user-facing abstractions, it must include a comment describing the collection."