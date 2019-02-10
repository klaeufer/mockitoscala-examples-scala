import org.mockito.IdiomaticMockito
import org.scalatest.{ Matchers, WordSpec }

class IteratorMapIsLazy extends WordSpec with Matchers with IdiomaticMockito {

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
        val it = spy(Iterator.continually("hello"))
        val result = it.map(_.length)
        it.next() wasNever called
        result.next()
        it.next() wasCalled once
      }
    }
  }
}
