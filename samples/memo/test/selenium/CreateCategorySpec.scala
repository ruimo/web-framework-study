package selenium

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import org.openqa.selenium.firefox.FirefoxDriver

class CreateCategorySpec extends Specification {
  "カテゴリの作成" should {
    "何も入力せずにsubmitすると必須エラーが表示されること" in {
      running(new TestServer(
        port = 3333,
        application = FakeApplication(additionalConfiguration = inMemoryDatabase())
      ), classOf[FirefoxDriver])
      { browser =>
        browser.goTo("http://localhost:3333/startCreateCategory")
        browser.title === "カテゴリの作成"

        browser.submit("form")
        browser.$("div.error div.input span.help-inline").getText == "Required"
      }
    }

    "カテゴリが登録されること" in {
      running(new TestServer(
        port = 3333,
        application = FakeApplication(additionalConfiguration = inMemoryDatabase())
      ), classOf[FirefoxDriver])
      { browser =>
        browser.goTo("http://localhost:3333/startCreateCategory")
        browser.find("#name").text("My category")
        browser.submit("form")
        browser.$("#categoryList tr td").getText === "My category"
      }
    }
  }
}


