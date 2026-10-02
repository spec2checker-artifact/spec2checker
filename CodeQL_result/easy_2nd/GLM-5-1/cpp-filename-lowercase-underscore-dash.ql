/**
 * CodeQL 查询: cpp-filename-lowercase-underscore-dash
 * 描述: Filenames should be all lowercase and can include underscores (_) or dashes (-).
 * 严重级别: WARNING
 *
 * 语言: cpp
 */

import cpp

from File f
where
    f.getBaseName().regexpMatch("[A-Z].*") or
    not f.getBaseName().regexpMatch("[a-z0-9_\\-]+\\.\\w+")
select f, "Filename '" + f.getBaseName() + "' should be all lowercase with underscores or dashes only."