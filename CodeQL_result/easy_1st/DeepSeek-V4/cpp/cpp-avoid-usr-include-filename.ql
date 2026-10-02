/**
 * CodeQL 查询: cpp-avoid-usr-include-filename
 * 描述: Do not use filenames that already exist in the system directory /usr/include, such as db.h. Use more specific names.
 * 严重级别: WARNING
 *
 * 语言: cpp
 */

import cpp

from File f
where
    f.getBaseName().regexpMatch("db\\.h|langinfo\\.h|nl_types\\.h|uchar\\.h|regex\\.h|stdlib\\.h")
select f, "Do not use filenames that already exist in /usr/include (such as " + f.getBaseName() + "). Use more specific names."
