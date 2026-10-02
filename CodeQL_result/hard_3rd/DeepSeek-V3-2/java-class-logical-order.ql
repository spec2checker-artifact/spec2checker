/**
 * CodeQL 查询: java-class-logical-order
 * 描述: Class members and initializers should follow a logical order that the maintainer could explain, not chronological by date added. Do not habitually add new methods to the end.
 * 严重级别: WARNING
 *
 * 语言: java
 */

import java

from Field f, Method m
where f.getDeclaringType() = m.getDeclaringType()
  and m.getName().regexpMatch("^get[A-Z].*|^set[A-Z].*|^is[A-Z].*")
  and f.getLocation().getStartLine() > m.getLocation().getStartLine()
select f, "Field '" + f.getName() + "' appears after getter/setter '" + m.getName() + "'. Class members should follow logical order: constants, fields, constructors, then methods."