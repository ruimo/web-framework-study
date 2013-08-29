package models

import play.api.db._
import anorm._
import anorm.SqlParser._
import play.api.Play.current

case class Category(
  id: Pk[Long] = NotAssigned,
  name: String
) extends NotNull {
  require(name != null)

  def set(
    id: Pk[Long] = this.id,
    name: String = this.name
  ) = Category(id, name)
}

object Category {
  val simple = {
    get[Pk[Long]]("category.id") ~
    get[String]("category.name") map {
      case id~name => Category(id, name)
    }
  }

  def findById(id: Long): Option[Category] =
    DB.withConnection { implicit conn =>
      SQL("select * from category where id = {id}")
        .on('id -> id)
        .as(simple.singleOpt)
    }

  def listCategory(from: Int = 0, rows: Int = 20): Seq[Category] =
    DB.withConnection { implicit conn =>
      SQL("""
          select * from category order by id
          limit {rows} offset {from}
          """
      ).on(
        'rows -> rows,
        'from -> from
      ).as(simple *)
    }

  def insert(category: Category): Option[Long] =
    DB.withConnection { implicit conn =>
      SQL("""
          insert into category values (
            (select next value for category_seq),
            {name}
          )""")
        .on('name -> category.name)
        .executeInsert()
    }

  def categoryOptions: Seq[(String, String)] = DB.withConnection { implicit connection =>
    SQL("select * from category order by name").as(Category.simple *).map {
      c => c.id.toString -> c.name
    }
  }
}
