import org.mockito.IdiomaticMockito
import org.scalatest.{ Matchers, WordSpec }

import scala.collection.{ GenIterable, IterableLike, mutable }
import scala.runtime.ScalaRunTime.stringOf

class FurtherStatefulIteratorTests extends WordSpec with Matchers with IdiomaticMockito {

  "Iterator.sliding" when {

    // IteratorTests.scala
    "tested using mutable state" should {

      "not ask for unneeded element" in {
        var counter = 0
        val it = new Iterator[Int] {
          var i = 0
          def hasNext = {
            println("hasNext " + counter)
            counter = i
            true
          }
          def next = {
            println("next " + counter)
            i += 1
            i
          }
        }
        val slidingIt = it sliding 2
        slidingIt.next
        // Counter should be one, that means we didn't look further than needed
        counter shouldBe 1
      }
    }

    "tested using spy" should {

      "not ask for unneeded element" in {
        val it = spy(Iterator.continually("anyRef"))
        val slidingIt = it sliding 2
        slidingIt.next()
        it.next() wasCalled twice
      }
    }
  }

  "Iterator" when {

    "tested using mutable state" should {

      // scala/bug#9623
      "not excessively check hasNext in iteration/join/concatenation" in {
        var counter = 0
        val exp = List(1, 2, 3, 1, 2, 3)
        def it: Iterator[Int] = new Iterator[Int] {
          val parent = List(1, 2, 3).iterator
          def next(): Int = parent.next
          def hasNext: Boolean = {
            counter += 1
            parent.hasNext
          }
        }
        // Iterate separately
        val res = new mutable.ArrayBuffer[Int]
        it.foreach(res += _)
        it.foreach(res += _)
        assertSameElements(exp, res)
        counter shouldBe 8
        // JoinIterator
        counter = 0
        res.clear
        (it ++ it).foreach(res += _)
        assertSameElements(exp, res)
        counter shouldBe 8 // was 17
        // ConcatIterator
        counter = 0
        res.clear
        (Iterator.empty ++ it ++ it).foreach(res += _)
        assertSameElements(exp, res)
        counter shouldBe 8 // was 14
      }

      "be sufficiently lazy in toStream" in {
        val results = collection.mutable.ListBuffer.empty[Int]
        def mkIterator = (1 to 5).iterator map (x => {
          results += x; x
        })
        def mkInfinite = Iterator continually {
          results += 1; 1
        }
        // Stream is strict in its head so we should see 1 from each of them.
        val s1 = mkIterator.toStream
        val s2 = mkInfinite.toStream
        // back and forth without slipping into nontermination.
        results += (Stream from 1).toIterator.drop(10).toStream.drop(10).toIterator.next()
        assertSameElements(List(1, 1, 21), results)
      }
    }

    "tested using spy" should {

      "not excessively check hasNext in separate iterations" in {
        val exp = List(1, 2, 3, 1, 2, 3)
        def it = spy(Iterator(1, 2, 3))
        val i0 = it
        val r0 = i0.toList
        val i1 = it
        val r1 = i1.toList
        assertSameElements(exp, r0 ++ r1)
        i0.hasNext wasCalled fourTimes
        i1.hasNext wasCalled fourTimes
      }

      "not excessively check hasNext in join" in {
        val exp = List(1, 2, 3, 1, 2, 3)
        def it = spy(Iterator(1, 2, 3))
        val res = new mutable.ArrayBuffer[Int]
        val i0 = it
        val i1 = it
        (i0 ++ i1).foreach(res += _)
        assertSameElements(exp, res)
        i0.hasNext wasCalled fourTimes
        i1.hasNext wasCalled fourTimes
      }

      "not excessively check hasNext in concatenation" in {
        val exp = List(1, 2, 3, 1, 2, 3)
        def it = spy(Iterator(1, 2, 3))
        val res = new mutable.ArrayBuffer[Int]
        val i0 = it
        val i1 = it
        (Iterator.empty ++ i0 ++ i1).foreach(res += _)
        assertSameElements(exp, res)
        i0.hasNext wasCalled fourTimes
        i1.hasNext wasCalled fourTimes
      }

      "be sufficiently lazy in toStream" in {
        val iteratorFunc = spyLambda((x: Int) => "anyRef")
        val infiniteFunc = spyLambda(() => "anyRef")
        (1 to 5).iterator.map(iteratorFunc).toStream
        Iterator.continually(infiniteFunc()).toStream
        iteratorFunc.apply(1) wasCalled once
        infiniteFunc.apply() wasCalled once
      }

      "go back and forth correctly between iterator and stream" in {
        (Stream from 1).toIterator.drop(10).toStream.drop(10).toIterator.next() shouldBe 21
      }
    }
  }

  def assertSameElements[A, B >: A](expected: IterableLike[A, _], actual: GenIterable[B], message: String = ""): Unit =
    if (!(expected sameElements actual))
      fail(
        f"${if (message.nonEmpty) s"$message " else ""}expected:<${stringOf(expected)}> but was:<${stringOf(actual)}>")

  /**
   * Convenient for testing iterators.
   */
  def assertSameElements[A, B >: A](expected: IterableLike[A, _], actual: Iterator[B]): Unit =
    assertSameElements(expected, actual.toList, "")
}
