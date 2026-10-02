/**
 * CodeQL query: python-indentation-4-spaces-no-tabs
 * Description: Indent code blocks with 4 spaces. Never use tabs.
 * Implied line continuation should align wrapped elements vertically
 * or use hanging 4-space indent.
 * Severity: WARNING
 *
 * Language: python
 */

import python

from Line l
where
    l.getText().regexpMatch("(?s)^\t.*")
    and not l.getFile().getBaseName().regexpMatch(".*__pycache__.*")
select l, "Line uses tab character for indentation. Use 4 spaces instead."