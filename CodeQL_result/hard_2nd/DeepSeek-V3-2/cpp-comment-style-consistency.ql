/**
 * CodeQL 查询: cpp-comment-style-consistency
 * 描述: Use either `//` or `/* */` comment syntax consistently within the file. `//` is much more common.
 * 严重级别: WARNING
 *
 * 语言: cpp
 */

import cpp

from File f, Comment c1, Comment c2
where
    c1.getFile() = f and
    c2.getFile() = f and
    c1 != c2 and
    c1.getText().regexpMatch("/\\*.*") and
    c2.getText().regexpMatch("//.*")
select f, "File uses both `//` and `/* */` comment styles; use one consistently."