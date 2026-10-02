import python

from Call c
where
    exists(CallExpr ce |
        ce = c
        and exists(Keyword kw |
            kw = ce.getAKeywordArgument()
            and kw.getLocation().getStartColumn() - ce.getLocation().getStartColumn() > 3
        )
    )
    and not exists(Expr e |
        e = c.getAnArgument()
        and e.getLocation().getStartColumn() = c.getLocation().getStartColumn() + 1
    )
select c, "No whitespace before punctuation inside parentheses."