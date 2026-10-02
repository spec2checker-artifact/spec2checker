import python

from Name n
where
    n.getId().regexpMatch("^[a-z]{1,2}$") and
    not n.getId() in ["i", "j", "k", "x", "y", "z", "e", "f", "n", "m", "id", "ex", "ok", "fp", "ip", "db", "os", "io", "pi", "ui"]
select n, "Avoid ambiguous abbreviation: '" + n.getId() + "'"
