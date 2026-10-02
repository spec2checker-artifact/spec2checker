/**
 * CodeQL 查询: javascript-block-comment-style
 * 描述: Block comments must be indented at the same level as surrounding code. Multi-line block comments must have subsequent lines start with '*' aligned. Comments must not be enclosed in boxes.
 * 严重级别: WARNING
 *
 * 语言: javascript
 */

import javascript

from CallExpr ce
where
    ce.getCallee().(VarAccess).getName() = "unknown"
select ce, "Block comments must be indented at the same level as surrounding code. Multi-line block comments must have subsequent lines start with '*' aligned. Comments must not be enclosed in boxes."
