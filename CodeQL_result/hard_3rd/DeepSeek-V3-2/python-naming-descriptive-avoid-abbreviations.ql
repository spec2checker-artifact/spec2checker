/**
 * CodeQL 查询: python-naming-descriptive-avoid-abbreviations
 * 描述: Names should be descriptive. Avoid abbreviations that are ambiguous or unfamiliar.
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from Function f
where f.getName().regexpMatch("^[a-z]{1,3}$")
  and not f.getName().regexpMatch("^(run|add|get|set|put|del|pop|map|cmp|chk|app|log|i|j|k|x|y|z)$")
select f, "Avoid short/ambiguous abbreviation '" + f.getName() + "'. Use descriptive names."