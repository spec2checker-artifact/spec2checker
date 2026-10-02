/**
 * CodeQL 查询: python-whitespace-no-trailing-whitespace
 * 描述: Detect lines that end with whitespace characters (spaces or tabs).
 * 严重级别: WARNING
 *
 * 语言: python
 */

import python

from File f, int lineNum, string lineText
where
    lineNum = f.getNumberOfLines() and
    lineNum > 0 and
    exists(int i | i = f.getBaseName().length() | i > 0)
select f, "Detect lines that end with whitespace characters (spaces or tabs)."
