/**
 * CodeQL 查询: java-multiple-consecutive-blank-lines
 * 描述: Detect multiple consecutive blank lines. Permitted but never required or encouraged.
 * 严重级别: WARNING
 *
 * 语言: java
 */

import java

from File f, int line1, int line2
where
    line2 = line1 + 3 and
    exists(Expr e1 | e1.getFile() = f and e1.getLocation().getStartLine() = line1) and
    not exists(Expr e2 | e2.getFile() = f and e2.getLocation().getStartLine() = line1 + 1) and
    not exists(Expr e3 | e3.getFile() = f and e3.getLocation().getStartLine() = line1 + 2)
select f, f.getBaseName() + ":" + line1.toString(), "Multiple consecutive blank lines detected"
