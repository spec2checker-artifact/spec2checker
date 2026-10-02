/**
 * CodeQL 查询: python-whitespace-no-space-before-punctuation
 * 描述: No whitespace before a comma, semicolon, or colon. Use space after them.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Comment c
where
    c.getText().matches("%TODO%")
select c, "This rule requires formatting checks which CodeQL cannot handle"
