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
    f.getBaseName().regexpMatch("db\\.h|strings\\.h|signal\\.h|unistd\\.h|errno\\.h|fcntl\\.h|pthread\\.h|stdlib\\.h|stdio\\.h|math\\.h|time\\.h|ctype\\.h|assert\\.h")
select f, "Do not use filenames that already exist in the system directory /usr/include, such as db.h. Use more specific names."