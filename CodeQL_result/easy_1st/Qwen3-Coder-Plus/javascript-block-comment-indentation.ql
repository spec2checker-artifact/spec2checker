/**
 * CodeQL 查询: javascript-block-comment-indentation
 * 描述: Block comments must be indented at the same level as the surrounding code. Multi-line block comments must have subsequent lines start with '*' aligned.
 * 严重级别: WARNING
 *
 * 语言: javascript
 */

import javascript

from Comment c
where
    c.getText().matches("%/*%*/%") and
    c.getStartLine() != c.getEndLine()
select c, "Block comments must be indented at the same level as the surrounding code. Multi-line block comments must have subsequent lines start with '*' aligned."