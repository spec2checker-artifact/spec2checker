/**
 * CodeQL 查询: java-parallel-continuation-indent
 * 描述: Two continuation lines must use the same indentation level if they begin with syntactically parallel elements, such as arguments on separate lines aligning.
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
select call, call.getFile().toString() + ":" + call.getLocation().getStartLine().toString(), "Two continuation lines must use the same indentation level if they begin with syntactically parallel elements, such as arguments on separate lines aligning."
