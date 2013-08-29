package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import models.{Category, Memo}
import anorm.{NotAssigned, Pk}

object Application extends Controller {
  val categoryForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "name" -> text.verifying(nonEmpty)
    )(Category.apply)(Category.unapply)
  )
  
  val memoForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "memo" -> text.verifying(nonEmpty),
      "created" -> ignored(0: Long),
      "category" -> longNumber
    )(Memo.apply)(Memo.unapply)
  )

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def startCreateCategory = Action { implicit request =>
    Ok(views.html.createCategory(categoryForm, Category.listCategory()))
  }

  def createCategory = Action { implicit request =>
    categoryForm.bindFromRequest.fold(
      formWithErrors =>
        BadRequest(views.html.createCategory(formWithErrors, Category.listCategory())),

      value => {
        Category.insert(value)
        Redirect(routes.Application.startCreateCategory)
          .flashing("message" -> ("カテゴリ " + value.name + " が追加されました。"))
      }
    )
  }

  def startCreateMemo = Action { implicit request =>
    Ok(views.html.createMemo(memoForm, Memo.listWithCategory(), Category.categoryOptions))
  }

  def createMemo = Action { implicit request =>
    memoForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.createMemo
                   (formWithErrors, Memo.listWithCategory(), Category.categoryOptions))
      },

      value => {
        Memo.insert(value)
        Redirect(routes.Application.startCreateMemo)
          .flashing("message" -> ("メモ " + value.memo + " が追加されました。"))
      }
    )
  }

  def startMemo = Action {
    Ok(views.html.list())
  }
}
