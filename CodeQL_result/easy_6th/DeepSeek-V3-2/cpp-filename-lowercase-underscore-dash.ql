/**
 * CodeQL 查询: cpp-filename-lowercase-underscore-dash
 * 描述: Filenames should be all lowercase and can include underscores (_) or dashes (-). Follow the project's convention. If no consistent pattern, prefer _. C++ files use .cc, headers use .h. Files textually included at specific points use .inc.
 * 严重级别: WARNING
 *
 * 语言: cpp
 */

import cpp

from File f
where
    not f.getBaseName().regexpMatch("[a-z0-9_\\-]+\\.(h|cc|cpp|cxx|c|hpp|hxx|inc)")
    and f.getExtension().regexpMatch("(h|cc|cpp|cxx|c|hpp|hxx|inc)")
select f, "Filename '" + f.getBaseName() + "' should be all lowercase with underscores or dashes only."