/**
 * CodeQL 查询: cpp-filename-lowercase-underscore-dash
 * 描述: Filenames should be all lowercase and can include underscores (_) or dashes (-).
 * 严重级别: WARNING
 */

import cpp

from File f
where
    f.getBaseName().regexpMatch(".*[A-Z].*")
    and not f.getBaseName().regexpMatch(".*\\.inc$")
    and (
        f.getExtension() = "h" or
        f.getExtension() = "hpp" or
        f.getExtension() = "cc" or
        f.getExtension() = "cpp" or
        f.getExtension() = "cxx" or
        f.getExtension() = "c"
    )
select f, f.getBaseName(), "Filenames should be all lowercase and can include underscores (_) or dashes (-). C++ files use .cc, headers use .h."