/**
 * CodeQL 查询: java-parallel-continuation-indent
 * 描述: Two continuation lines must use the same indentation level if they begin with syntactically parallel elements, such as arguments on separate lines aligning.
 * 严重级别: WARNING
 *
 * 语言: java
 */

import java

from MethodCall call, Expr arg1, Expr arg2
where
    arg1 = call.getAnArgument() and
    arg2 = call.getAnArgument() and
    arg1 != arg2 and
    arg1.getLocation().getStartLine() + 1 = arg2.getLocation().getStartLine() and
    arg1.getLocation().getStartColumn() != arg2.getLocation().getStartColumn()
select arg2, "Continuation line at " + arg2.getLocation().getStartLine().toString() + " has different indentation than previous argument. Parallel elements should align."