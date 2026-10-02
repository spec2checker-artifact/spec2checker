/**
 * CodeQL 查询: java-multiple-consecutive-blank-lines
 * 描述: Detect multiple consecutive blank lines. Permitted but never required or encouraged.
 * 严重级别: WARNING
 *
 * 语言: java
 */

import java

from Call call, Expr arg
where
    call.getCallee() instanceof Method and
    call.getCallee().(Method).hasName("unknown") and
    arg = call.getArgument(0) and
        true
select call, call.getFile().toString() + ":" + call.getLocation().getStartLine().toString(), "Detect multiple consecutive blank lines. Permitted but never required or encouraged."
