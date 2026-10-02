/**
 * CodeQL 查询: python-whitespace-no-vertical-alignment
 * 描述: Don't use spaces to vertically align tokens on consecutive lines; it becomes a maintenance burden.
 * 严重级别: WARNING
 *
 * 语言: python
 * 检测连续行中赋值语句存在多余空格对齐(3+空格在等号前)
 */

import python

from Line l1, Line l2, File f
where
    l1.getFile() = f
    and l2.getFile() = f
    and f.getBaseName().regexpMatch(".+\\.py")
    and l1.getNumber() + 1 = l2.getNumber()
    and l1.getText().regexpMatch(".*\\w\\s{3,}=\\s.*")
    and l2.getText().regexpMatch(".*\\w\\s{3,}=\\s.*")
    and not l1.getText().regexpMatch("^\\s*#.*")
    and not l2.getText().regexpMatch("^\\s*#.*")
select l1, f, "Don't use spaces to vertically align tokens on consecutive lines; it becomes a maintenance burden. Lines " + l1.getNumber() + " and " + l2.getNumber() + " in " + f.getBaseName() + "."