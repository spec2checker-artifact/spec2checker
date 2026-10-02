/**
 * CodeQL 查询: javascript-line-wrap-after-operator
 * 描述: When a line is broken at an operator, the break must come after the operator symbol. The dot (.) is not considered an operator for this rule.
 * 严重级别: WARNING
 *
 * 语言: javascript
 */

import javascript

from BinaryExpr bin
where
    bin.getAnOperand().getStartLine() > bin.getOperator().getStartLine()
select bin, "When a line is broken at an operator, the break must come after the operator symbol. The dot (.) is not considered an operator for this rule."
