/**
 * CodeQL 查询: python-string-quote-consistency
 * 描述: Be consistent with string quote character within a file. Pick ' or " and stick with it. Use the other quote to avoid escaping.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from File f
where exists(StringLiteral s | s.getFile() = f and s.getText().regexpMatch("^'"))
  and exists(StringLiteral s | s.getFile() = f and s.getText().regexpMatch("^\""))
select f, f.getBaseName(), "Be consistent with string quote character within a file. Pick ' or \" and stick with it."