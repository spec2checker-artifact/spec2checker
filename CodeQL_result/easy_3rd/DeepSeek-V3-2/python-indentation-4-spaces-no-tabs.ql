/**
 * CodeQL 查询: python-indentation-4-spaces-no-tabs
 * 描述: Indent code blocks with 4 spaces. Never use tabs. Implied line continuation should align wrapped elements vertically or use hanging 4-space indent.
 * 严重级别: WARNING
 *
 * 语言: python
 * 检测Python源文件中包含tab缩进字符的具体行
 */

import python

from File f, Line l
where
    f.getBaseName().regexpMatch(".+\\.py")
    and l.getFile() = f
    and l.getNumber() > 0
    and l.getText().regexpMatch("^\\t+.*")
select l, f, "Indent code blocks with 4 spaces. Never use tabs. Found tab character at line " + l.getNumber() + " in " + f.getBaseName() + "."