/**
 * CodeQL query: javascript-line-wrap-after-operator
 * Description: When a line is broken at an operator, the break must come after the operator symbol.
 * Severity: WARNING
 *
 * Language: javascript
 */

import javascript

from BinaryExpr binop
where
  binop.getOperator().getStartLine() > binop.getLeftOperand().getEndLine()
  and not binop.getOperator().toString() = "."
select binop, "Line break before operator '" + binop.getOperator().toString() + "' - break should come after the operator."