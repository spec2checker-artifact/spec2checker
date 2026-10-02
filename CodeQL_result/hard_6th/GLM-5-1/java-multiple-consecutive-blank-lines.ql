/**
 * CodeQL 查询: java-multiple-consecutive-blank-lines
 * 描述: Detect multiple consecutive blank lines. Permitted but never required or encouraged.
 * 严重级别: WARNING
 *
 * 语言: java
 */

import java

from Method m, Method m2
where
    m.getFile() = m2.getFile() and
    m != m2 and
    m2.getLocation().getStartLine() - m.getLocation().getEndLine() > 2
select m, "Multiple consecutive blank lines after method at line " + m.getLocation().getEndLine().toString() + ". Permitted but never required or encouraged."