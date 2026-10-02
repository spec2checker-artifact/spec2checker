/**
 * CodeQL 查询: cpp-comment-style-consistency
 * 描述: Use either `//` or `/* */` comment syntax consistently within a file. `//` is much more common.
 * 严重级别: WARNING
 *
 * 语言: cpp
 */

import cpp

from Comment c
where
    c.getText().matches("%*%*/%") and
    not c.getText().matches("//%")
select c, "Use either `//` or `/* */` comment syntax consistently within a file. `//` is much more common."