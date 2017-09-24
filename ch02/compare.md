
Which ADT is used to implement `$`-notation
---

1. Future
  - pros
    + built-in
    + easy(but not simple)
  - cons
    + not lazy(just async)

2. cats.Eval
  - pros
    + fit in purpose
    + already known
  - cons
    + external library

3. monix.Task
  - pros
    + fit in purpose
    + I'm interested in monix
  - cons
    + external libray
    + too much background knowledge
