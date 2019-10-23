import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito, MockitoScalaSession }
import org.scalatest.{ Matchers, WordSpec }

class TestIssue10709 extends WordSpec with Matchers with IdiomaticMockito with ArgumentMatchersSugar {

  def scanLeftUsingIterate[T, U](it: Iterator[T])(z: U)(op: (U, T) => U): Iterator[U] =
    Iterator.iterate(Option(z)) {
      case Some(r) =>
        if (it.hasNext) Option(op(r, it.next())) else None
    } takeWhile (_.isDefined) map (_.get)

  def verifyIncremental(sut: Iterator[Int] => Iterator[Int]): Unit = {
    val it = spy(Iterator(1, 2, 3))
    val expected = Array(0, 1, 3, 6)
    val result = sut(it)
    for (i <- expected.indices) {
      result.next() shouldBe expected(i)
      it.next() wasCalled i.times
    }
  }

  // TODO make more DRY

  "Iterator.scanLeft" when {

    "given an empty iterator" should {

      "produce the correct a singleton result" in {
        val result = Seq.empty[Int].iterator.scanLeft(0)(_ + _)
        result.toSeq shouldBe Seq(0)
      }
    }

    "given a nonempty iterator" should {

      "produce the correct cumulative result" in {
        val result = Iterator(1, 2, 3).scanLeft(0)(_ + _)
        result.toSeq shouldBe Seq(0, 1, 3, 6)
      }

      "exhibit the correct incremental element-by-element behavior" in
        verifyIncremental(_.scanLeft(0)(_ + _))

      // Ignoring this test because it includes interactions inside the black box,
      // where we have insufficient access to verify these.
      // They are in the implementation of Iterator over an explicit vararg/Seq of items:
      // elements.scala$collection$IndexedSeqLike$Elements$$$outer(); ->
      //   at scala.collection.IndexedSeqLike$Elements.next(IndexedSeqLike.scala:65)
      "exhibit the correct incremental element-by-element behavior (strict spy version)" ignore MockitoScalaSession().run {
        val it = spy(Iterator(1, 2, 3))
        val expected = Array(0, 1, 3, 6)
        val result = it.scanLeft(0)(_ + _)
        for (i <- expected.indices) {
          result.next() shouldBe expected(i)
          it.next() wasCalled i.times
        }
        it.scanLeft(*)(*) wasCalled once
      }
    }
  }

  "scanLeftUsingIterate" when {

    "given an empty iterator" should {

      "produce the correct a singleton result" in {
        val result = scanLeftUsingIterate(Seq.empty[Int].iterator)(0)(_ + _)
        result.toSeq shouldBe Seq(0)
      }
    }

    "given a nonempty iterator" should {

      "produce the correct cumulative result" in {
        val result = scanLeftUsingIterate(Iterator(1, 2, 3))(0)(_ + _)
        result.toSeq shouldBe Seq(0, 1, 3, 6)
      }

      "exhibit the correct incremental element-by-element behavior" in
        verifyIncremental(input => scanLeftUsingIterate(input)(0)(_ + _))
    }
  }
}
