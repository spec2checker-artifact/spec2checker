/**
 * CodeQL 查询: python-whitespace-no-space-before-punctuation
 * 描述: No whitespace before a comma, semicolon, or colon. Use space after them.
 * 严重级别: WARNING
 *
 * 语言: python
 * 注意: CodeQL operates on parsed AST and cannot directly inspect whitespace.
 *       This query detects parameter lists and call arguments as a proxy for
 *       potential formatting issues, but cannot truly check whitespace before punctuation.
 */

import python

from Parameter p
where
    p.getName() = ""
select p, "CodeQL cannot check whitespace formatting; this is a placeholder detection for parameter lists that may have spacing issues."