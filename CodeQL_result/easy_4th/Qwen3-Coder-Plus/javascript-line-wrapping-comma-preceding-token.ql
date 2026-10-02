import javascript

from ObjectLiteral ol, Property p1, Property p2
where
    p1.getParent() = ol
    and p2.getParent() = ol
    and p1 != p2
    and p1.getName().regexpMatch("^['\"].*['\"]$")
    and not p2.getName().regexpMatch("^['\"].*['\"]$")
select ol, "A comma must stay attached to the token that precedes it when line-wrapping. Mixed quoted/unquoted keys found."