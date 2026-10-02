/**
 * CodeQL 查询: javascript-block-comment-style
 * 描述: Block comments must be indented at the same level as surrounding code.
 * 严重级别: WARNING
 *
 * 语言: javascript
 */

import javascript

from Comment c
where
    c.getText().regexpMatch("/\\*.*\\*/") and
    c.getText().length() > 4 and
    c.getLocation().getStartColumn() > 1
select c, "Block comment is not indented at the same level as surrounding code."
