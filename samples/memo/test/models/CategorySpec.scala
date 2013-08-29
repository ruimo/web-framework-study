package models

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class CategorySpec extends Specification {
  "Category" should {
    "挿入したレコードを照会できる" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val optId = Category.insert(Category(name = "買い物"))
        val optCat = Category.findById(optId.get)
        optCat.get.name === "買い物"
      }
    }

    "リスト取得" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        Category.insert(Category(name = "Category1"))
        Category.insert(Category(name = "Category2"))

        val recs = Category.listCategory()
        recs(0).name === "Category1"
        recs(1).name === "Category2"
      }
    }
  }
}
