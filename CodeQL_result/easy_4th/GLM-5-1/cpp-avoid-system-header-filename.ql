/**
 * CodeQL 查询: cpp-avoid-system-header-filename
 * 描述: Do not use filenames that already exist in /usr/include, such as db.h. Use very specific filenames.
 * 严重级别: WARNING
 *
 * 语言: cpp
 */

import cpp

from PreprocessorInclude incl
where
    incl.getHeaderFile().getBaseName() = "db.h"
    or incl.getHeaderFile().getBaseName() = "math.h"
    or incl.getHeaderFile().getBaseName() = "stdio.h"
    or incl.getHeaderFile().getBaseName() = "stdlib.h"
    or incl.getHeaderFile().getBaseName() = "string.h"
    or incl.getHeaderFile().getBaseName() = "time.h"
    or incl.getHeaderFile().getBaseName() = "errno.h"
    or incl.getHeaderFile().getBaseName() = "signal.h"
    or incl.getHeaderFile().getBaseName() = "assert.h"
    or incl.getHeaderFile().getBaseName() = "ctype.h"
    or incl.getHeaderFile().getBaseName() = "fcntl.h"
    or incl.getHeaderFile().getBaseName() = "unistd.h"
    or incl.getHeaderFile().getBaseName() = "pthread.h"
    or incl.getHeaderFile().getBaseName() = "regex.h"
    or incl.getHeaderFile().getBaseName() = "locale.h"
    or incl.getHeaderFile().getBaseName() = "float.h"
    or incl.getHeaderFile().getBaseName() = "limits.h"
    or incl.getHeaderFile().getBaseName() = "setjmp.h"
    or incl.getHeaderFile().getBaseName() = "stdarg.h"
    or incl.getHeaderFile().getBaseName() = "stddef.h"
    or incl.getHeaderFile().getBaseName() = "wchar.h"
    or incl.getHeaderFile().getBaseName() = "wctype.h"
    or incl.getHeaderFile().getBaseName() = "iconv.h"
    or incl.getHeaderFile().getBaseName() = "poll.h"
    or incl.getHeaderFile().getBaseName() = "termios.h"
    or incl.getHeaderFile().getBaseName() = "arpa/inet.h"
    or incl.getHeaderFile().getBaseName() = "netdb.h"
    or incl.getHeaderFile().getBaseName() = "sys/wait.h"
select incl, "Do not use filenames that already exist in /usr/include, such as $@. Use very specific filenames.", incl.getHeaderFile().getBaseName(), incl.getHeaderFile().getBaseName()