import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito, MockitoScalaSession }
import org.scalatest.{ Matchers, WordSpec }

class IteratorMapIsLazy extends WordSpec with Matchers with IdiomaticMockito with ArgumentMatchersSugar {

  "Iterator.map" when {

    "tested using mutable state" should {

      "be lazy" in {
        var counter = 0
        val it = Iterator.continually {
          counter += 1
          counter
        }
        val result = it.map(_ + 1)
        counter shouldBe 0
        result.next()
        counter shouldBe 1
      }
    }

    "when tested via a spy" should {

      "be lazy" in {
        val it = spy(Iterator.continually("anyRef"))
        val result = it.map(_.length)
        it.next() wasNever called
        result.next()
        it.next() wasCalled once
      }

      "be lazy (strict spy version)" in {
        MockitoScalaSession().run {
          val it = spy(Iterator.continually("anyRef"))
          val result = it.map(_.length)
          it.next() wasNever called
          result.next()
          it.next() wasCalled once
          it.map(*) wasCalled once
          it.hasNext wasNever called
        }
      }
    }
  }
}
