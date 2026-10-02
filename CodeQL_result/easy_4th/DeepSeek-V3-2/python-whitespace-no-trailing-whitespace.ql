/**
 * CodeQL 查询: python-whitespace-no-trailing-whitespace
 * 描述: Detect lines that end with whitespace characters (spaces or tabs).
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Comment c
where
    c.getText().matches("%TODO%")
select c, "This rule requires formatting checks which CodeQL cannot handle"
