package functionals

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class CreateCategorySpec extends Specification {
  "Create Category" should {
    "Empty name results in bad request" in {
      running(FakeApplication()) {
        val page = route(FakeRequest(GET, "/createCategory")).get
        status(page) must equalTo(BAD_REQUEST)
        contentType(page) must beSome.which(_ == "text/html")
      }
    }

    "Create category success" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        models.Category.listCategory().isEmpty === true

        val page = route(FakeRequest(GET, "/createCategory?name=TestCategory")).get
        status(page) must equalTo(SEE_OTHER)
        redirectLocation(page) must beSome.which(_ == "/startCreateCategory")

        val recs = models.Category.listCategory()
        recs.size === 1
        recs.head.name === "TestCategory"
      }
    }
  }
}
