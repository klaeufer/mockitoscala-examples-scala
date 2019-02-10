[![Build Status](https://travis-ci.org/klaeufer/mockitoscala-examples-scala.svg?branch=master)](https://travis-ci.org/klaeufer/mockitoscala-examples-scala)

This project includes some examples of using Mockito Scala to express expectations declaratively.
For example, we can test whether the `Iterator.map` method is lazy in terms of
invoking `next` on the original object only when we invoke `next` on the result of `map`:

    val it = spy(Iterator.continually("anyRef"))
    val result = it.map(_.length)
    it.next() wasNever called
    result.next()
    it.next() wasCalled once

For a more in-depth discussion on this topic,
please refer to this technical report (5 pages):

*Auto-generated Spies Increase Test Maintainability* \
Konstantin Läufer, John O'Sullivan, and George K. Thiruvathukal \
https://arxiv.org/abs/1808.09630

Also, for the time being, make sure to use reference types as to instantiate any SUTs based on generic Scala traits; see also

https://github.com/mockito/mockito/issues/1605
