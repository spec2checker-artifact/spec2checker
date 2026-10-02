/**
 * CodeQL 查询: cpp-avoid-system-header-filename
 * 描述: Do not use filenames that already exist in /usr/include, such as db.h. Use very specific filenames.
 * 严重级别: WARNING
 *
 * 语言: cpp
 */

import cpp

from IncludeDirective inc
where
    inc.toString().regexpMatch(".*[<\"].*\\.h[>\"].*")
select inc, "Do not use filenames that already exist in /usr/include, such as db.h. Use very specific filenames."
