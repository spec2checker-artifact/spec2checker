/**
 * CodeQL query: python-indentation-4-spaces-no-tabs
 * Description: Indent code blocks with 4 spaces. Never use tabs.
 * Severity: WARNING
 *
 * Language: python
 */

import python

from SimpleStmt stmt
where
  exists(string rawLine |
    rawLine = stmt.getFile().getRelativePath() and
    stmt.getStartColumn() = 1
  )
  and stmt.getIndentation().regexpMatch("^\\t+")
select stmt, "Use 4 spaces for indentation, never tabs."