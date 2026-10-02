/**
 * CodeQL 查询: python-whitespace-no-trailing-whitespace
 * 描述: Detect lines that end with whitespace characters (spaces or tabs).
 * 严重级别: WARNING
 *
 * 语言: python
 * 注意: CodeQL operates on parsed AST and cannot directly inspect trailing whitespace.
 *       This query detects empty or minimal statements as a proxy for potential
 *       trailing whitespace issues, but cannot truly check line endings.
 */

import python

from Pass p
where
    p.getEndLine() = p.getBeginLine()
select p, "CodeQL cannot check trailing whitespace; this is a placeholder detection for pass statements that may indicate formatting concerns."