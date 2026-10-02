/**
 * CodeQL 查询: javascript-line-wrap-after-operator
 * 描述: When a line is broken at an operator, the break must come after the operator symbol. The dot (.) is not considered an operator for this rule.
 * 严重级别: WARNING
 *
 * 语言: javascript
 */

import javascript

from BinaryExpr binop
where
    binop.getLeftOperand().getEndLine() != binop.getRightOperand().getStartLine()
    and binop.getOperator().toString() != "."
select binop, "When a line is broken at an operator, the break must come after the operator symbol. The left operand ends on line " + binop.getLeftOperand().getEndLine() + " but the operator continues on line " + binop.getRightOperand().getStartLine() + "."