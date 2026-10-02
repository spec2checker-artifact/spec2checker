/**
 * CodeQL 查询: javascript-block-comment-indentation
 * 描述: Block comments must be indented at the same level as the surrounding code.
 * 严重级别: WARNING
 *
 * 语言: javascript
 */

import javascript

from Comment c
where
    not c.getBlockCommentContent().regexpMatch("%\n *%")
select c, "Block comments must be indented at the same level as the surrounding code."
