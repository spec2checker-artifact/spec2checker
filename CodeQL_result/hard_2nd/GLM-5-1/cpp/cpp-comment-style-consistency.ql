import cpp

from File f, MacroDefine md1, MacroDefine md2
where
    md1.getFile() = f and md2.getFile() = f and
    md1 != md2 and
    md1.getMacroName().regexpMatch("[A-Z_]+_H_") and
    md2.getMacroName().regexpMatch("[A-Z_]+_H_")
select f, "File has multiple include guards: " + md1.getMacroName() + " and " + md2.getMacroName()
