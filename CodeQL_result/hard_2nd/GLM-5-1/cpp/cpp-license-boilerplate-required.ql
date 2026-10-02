import cpp

from File f
where
    not exists(MacroDefine md | md.getFile() = f and md.getMacroName().regexpMatch("(?i)license|copyright|spdx")) and
    exists(Include inc | inc.getFile() = f)
select f, "File missing license boilerplate: " + f.getBaseName()
