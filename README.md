[![Build Status](https://travis-ci.org/klaeufer/mockitoscala-examples-scala.svg?branch=master)](https://travis-ci.org/klaeufer/mockitoscala-examples-scala)

This project includes some examples of using Mockito Scala to express expectations declaratively.
For example, we can test whether the `Iterator.map` method is lazy in terms of
invoking `next` on the original object only when we invoke `next` on the result of `map`:

    val it = spy(Iterator.continually("anyRef"))
    val result = it.map(_.length)
    it.next() wasNever called
    result.next()
    it.next() wasCalled once

For a more in-depth discussion on this topic, please refer to this paper (5 pages):

*Tests as Maintainable Assets Via Auto-generated Spies:* \
*A case study involving the Scala collections library's Iterator trait* \
Konstantin Läufer, John O'Sullivan, and George K. Thiruvathukal \
In Proc. [10th ACM SIGPLAN Scala Symposium, July 2019, London, UK](https://2019.ecoop.org/home/scala-2019#program).
https://arxiv.org/abs/1808.09630

Also, make sure to use reference types as to instantiate any SUTs based on generic Scala traits; see also

https://github.com/mockito/mockito/issues/1605
