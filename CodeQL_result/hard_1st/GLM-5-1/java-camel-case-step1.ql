/**
 * @name java-camel-case-step1
 * @description Convert phrase to plain ASCII and remove apostrophes as a preprocessing step for camel case conversion. Flags identifiers or string literals containing non-ASCII characters or apostrophes.
 * @kind problem
 * @problem.severity warning
 * @precision low
 * @id java/camel-case-step1
 * @tags maintainability
 */

import java

predicate hasNonAsciiOrApostrophe(string s) {
  exists(int i | i >= 0 and i < s.length() and
    (s.charAt(i) >= 128 or s.charAt(i) = 39))
}

from Expr e
where
  e instanceof StringLiteral and
  hasNonAsciiOrApostrophe(e.(StringLiteral).getValue())
select e, "String literal contains non-ASCII characters or apostrophes; convert to plain ASCII before camel casing."
