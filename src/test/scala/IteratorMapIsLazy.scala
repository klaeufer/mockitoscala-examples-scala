import org.mockito.IdiomaticMockito
import org.scalatest.{ Matchers, WordSpec }

class IteratorMapIsLazy extends WordSpec with IdiomaticMockito with Matchers {

  "Iterator.map" should {

    "be lazy when tested via mutable state" in {
      var counter = 0
      val it = Iterator.continually {
        counter += 1; counter
      }
      val result = it.map(_ + 1)
      counter shouldBe 0
      result.next()
      counter shouldBe 1
    }

    "be lazy when tested via a spy" in {
      val it = spy(Iterator.continually("hello"))
      val result = it.map(_.length)
      it.next() wasNever called
      result.next()
      it.next() wasCalled once
    }
  }
}
