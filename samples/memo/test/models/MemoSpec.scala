package models

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class MemoSpec extends Specification {
  "Memo" should {
    "挿入したメモを照会できる" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val memoOpt = Category.insert(Category(name = "買い物")) flatMap {
          id: Long => Memo.insert(Memo(memo = "牛乳を買う", categoryId = id))
        } flatMap {
          id: Long => Memo.findById(id)
        }

        memoOpt.get.memo === "牛乳を買う"
      }
    }

    "カテゴリと一緒に取り出す" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val startTime = System.currentTimeMillis

        val recOpt = Category.insert(Category(name = "買い物")) flatMap {
          id: Long => Memo.insert(Memo(memo = "牛乳を買う", categoryId = id))
        } flatMap {
          Memo.findWithCategoryById
        }

        val (memo: Memo, category: Category) = recOpt.get
        val endTime = System.currentTimeMillis

        memo.memo === "牛乳を買う"
        category.name === "買い物"
        memo.createdTime must between(startTime, endTime)
      }
    }
  }
}
