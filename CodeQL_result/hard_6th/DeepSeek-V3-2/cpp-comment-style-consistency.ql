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
    exists(Comment c1 | c1.getFile() = f and c1.getText().regexpMatch(".*//.*"))
    and exists(Comment c2 | c2.getFile() = f and c2.getText().regexpMatch(".*\\\\.*"))
select f, f.getBaseName() + ": mixes // and /* */ comment styles"
