/**
 * CodeQL 查询: java-multiple-consecutive-blank-lines
 * 描述: Detect multiple consecutive blank lines. Permitted but never required or encouraged.
 * 严重级别: WARNING
 *
 * 语言: java
 */

import java

from Method m
where
    m.getBody().getNumberOfLines() > 0 and
    exists(int i, int j |
        i = m.getBody().getStartLine() and
        j = m.getBody().getEndLine() and
        j - i > 40
    )
select m, "Detect multiple consecutive blank lines. Permitted but never required or encouraged."