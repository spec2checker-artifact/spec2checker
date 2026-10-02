/**
 * CodeQL 查询: javascript-block-comment-style
 * 描述: Block comments must be indented at the same level as surrounding code.
 *       Multi-line block comments must have subsequent lines start with '*' aligned.
 *       Comments must not be enclosed in boxes.
 * 严重级别: WARNING
 *
 * 语言: javascript
 * 注意: CodeQL operates on parsed AST and cannot directly inspect comment formatting.
 *       This query detects multi-line comments that may have formatting issues
 *       by checking comment content patterns.
 */

import javascript

from Comment c
where
    c.getText().regexpMatch("^[ \\t]*\\*[^/].*") and c.getText().length() > 20
select c, "Block comments must be indented at the same level as surrounding code. Multi-line block comments must have subsequent lines start with '*' aligned. Comments must not be enclosed in boxes."