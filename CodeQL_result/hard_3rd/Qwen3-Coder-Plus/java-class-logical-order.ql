import java

from MethodCall call, Expr arg1, Expr arg2
where
    arg1 = call.getAnArgument() and
    arg2 = call.getAnArgument() and
    arg1 != arg2 and
    arg1.getLocation().getStartLine() + 1 = arg2.getLocation().getStartLine() and
    arg1.getLocation().getStartColumn() != arg2.getLocation().getStartColumn()
select arg2, "Overloaded methods should appear in a single contiguous group. Method calls with misaligned arguments may indicate split overloads."