import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito, MockitoScalaSession, MockitoSugar }
import org.scalatest.{ Matchers, WordSpec }

class SimpleExample extends WordSpec with Matchers with IdiomaticMockito with ArgumentMatchersSugar {

  "Service" when {

    "tested" should {

      "work" in {

        trait Storage {
          def apply(name: String): Int
        }

        class Service(storage: Storage) {
          def lookup(name: String): Int = storage(name)
        }

        val store = mock[Storage]
        val service = new Service(store)
        store.apply("Scala") returns 2003

        service.lookup("Scala") shouldBe 2003
        store.apply("Scala") was called
      }
    }
  }
}
