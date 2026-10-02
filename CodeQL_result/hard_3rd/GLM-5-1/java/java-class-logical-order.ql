import java

from Field f, Method m
where
    f.getDeclaringType() = m.getDeclaringType() and
    f.getLocation().getStartLine() > m.getLocation().getStartLine()
select f, "Field '" + f.getName() + "' appears after method '" + m.getName() + "' in class. Members should follow logical order."
