/**
 * CodeQL 查询: python-statements-one-per-line
 * 描述: Generally only one statement per line. Exception allowed only if the entire statement (e.g., simple if) fits on one line. Never allowed for try/except or if/else on the same line.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Call c1, Call c2
where c1.getFile() = c2.getFile()
  and c1.getLocation().getStartLine() = c2.getLocation().getStartLine()
  and c1.getLocation().getStartColumn() < c2.getLocation().getStartColumn()
  and c1 != c2
select c1, "Generally only one statement per line. Multiple calls on the same line suggest multiple statements."