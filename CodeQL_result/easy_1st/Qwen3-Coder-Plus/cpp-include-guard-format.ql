/**
 * CodeQL 查询: cpp-include-guard-format
 * 描述: Use #define guards with format <PROJECT>_<PATH>_<FILE>_H_ based on full path in source tree.
 * 严重级别: WARNING
 *
 * 语言: cpp
 */

import cpp

from MacroDefine md
where
    md.getMacroName().regexpMatch("[A-Z]+_H_?$") and
    not md.getMacroName().regexpMatch("[A-Z]+_[A-Z]+_[A-Z]+_H_$")
select md, "Use #define guards with format <PROJECT>_<PATH>_<FILE>_H_ based on full path in source tree."