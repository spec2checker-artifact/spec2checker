/**
 * CodeQL 查询: java-parallel-continuation-indent
 * 描述: Two continuation lines must use the same indentation level if they begin with syntactically parallel elements, such as arguments on separate lines aligning.
 * 严重级别: WARNING
 *
 * 语言: java
 */

import java

from MethodCall mc, Expr arg1, Expr arg2
where
    arg1 = mc.getArgument(0) and
    arg2 = mc.getArgument(1) and
    arg1.getLocation().getStartLine() != arg2.getLocation().getStartLine() and
    arg1.getLocation().getStartColumn() != arg2.getLocation().getStartColumn()
select mc, "Two continuation lines must use the same indentation level if they begin with syntactically parallel elements, such as arguments on separate lines aligning."