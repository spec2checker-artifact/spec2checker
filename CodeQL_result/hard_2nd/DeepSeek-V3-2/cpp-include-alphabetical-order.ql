/**
 * CodeQL 查询: cpp-include-alphabetical-order
 * 描述: Within each include section, includes should be ordered alphabetically.
 * 严重级别: WARNING
 *
 * 语言: cpp
 */

import cpp

from Include i1, Include i2
where
    i1.getFile() = i2.getFile() and
    i1.getStartLine() < i2.getStartLine() and
    i1.getIncludePath() > i2.getIncludePath()
select i1, "Include '" + i1.getIncludePath() + "' should come after '" + i2.getIncludePath() + "' (alphabetical order)."