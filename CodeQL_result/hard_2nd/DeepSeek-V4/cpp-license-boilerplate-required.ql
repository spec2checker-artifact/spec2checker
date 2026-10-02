/**
 * CodeQL 查询: cpp-license-boilerplate-required
 * 描述: Every file should contain license boilerplate appropriate for the project. New files usually should not contain copyright notice or author line.
 * 严重级别: ERROR
 *
 * 语言: cpp
 */

import cpp

from File f
where
    not exists(Comment c |
        c.getFile() = f and
        c.getText().regexpMatch(".*Copyright.*|.*License.*|.*Licensed.*|.*SPDX.*|.*All rights reserved.*")
    )
    and f.getBaseName().regexpMatch(".*\\.(cpp|cxx|cc|c|h|hpp|hxx)$")
select f, "Every file should contain license boilerplate appropriate for the project."