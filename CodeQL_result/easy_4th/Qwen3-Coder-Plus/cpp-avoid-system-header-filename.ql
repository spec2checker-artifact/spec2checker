import cpp

from IncludeDirective inc
where
    inc.isFileInclude()
    and inc.getHeader().regexpMatch("^(stdlib|stdio|string|math|time|errno|ctype|signal|assert|locale|setjmp|stdarg)\\.h$")
select inc, "Do not use filenames that already exist in /usr/include. Use very specific filenames to avoid conflicts."