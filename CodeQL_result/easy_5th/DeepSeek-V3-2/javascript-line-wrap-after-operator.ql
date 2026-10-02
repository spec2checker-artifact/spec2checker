/**
 * CodeQL query: javascript-line-wrap-after-operator
 * Description: When a line is broken at an operator, the break must come after
 * the operator symbol. The dot (.) is not considered an operator for this rule.
 * Severity: WARNING
 *
 * Language: javascript
 */

import javascript

from BinaryExpr be, BinaryExpr parentBe
where
    be.getOperator().toString() = "&&"
    and be = parentBe.getRightOperand()
    and be.getLocation().getStartLine() > parentBe.getLocation().getStartLine()
select be, "Line break before operator '" + be.getOperator().toString() + "' violates the rule: break must come after the operator."