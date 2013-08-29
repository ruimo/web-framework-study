package models

import anorm._
import anorm.SqlParser._
import java.sql.Timestamp
import play.api.db.DB
import play.api.Play.current
import java.util.Date;

case class Memo(
  id: Pk[Long] = NotAssigned,
  memo: String,
  createdTime: Long = 0,
  categoryId: Long
) extends NotNull {
  require(memo != null)

  def set(
    id: Pk[Long] = this.id,
    memo: String = this.memo,
    createdTime: Long = this.createdTime,
    categoryId: Long = this.categoryId
  ) = Memo(id, memo, createdTime, categoryId)
}

object Memo {
  val simple = {
    get[Pk[Long]]("memo.id") ~
    get[String]("memo.memo") ~
    get[Date]("memo.created_time") ~
    get[Long]("memo.category_id") map {
      case id~memo~createdTime~categoryId =>
        Memo(id, memo, createdTime.getTime, categoryId)
    }
  }

  val withCategory = Memo.simple ~ Category.simple map {
    case memo~category => (memo, category)
  }

  def findWithCategoryById(id: Long): Option[(Memo, Category)] = 
    DB.withConnection { implicit conn =>
      SQL("""
          select * from memo
          inner join category on memo.category_id = category.id
          where memo.id = {id}
          """)
        .on(
          'id -> id
        ).as(withCategory.singleOpt)
    }

  def listWithCategory(from: Int = 0, rows: Int = 20): Seq[(Memo, Category)] =
    DB.withConnection { implicit conn =>
      SQL("""
          select * from memo
          inner join category on memo.category_id = category.id
          order by id
          """)
        .on(
          'rows -> rows,
          'from -> from
        ).as(withCategory *)
    }

  def findById(id: Long): Option[Memo] =
    DB.withConnection { implicit conn =>
      SQL("select * from memo where id = {id}")
        .on('id -> id)
        .as(simple.singleOpt)
    }

  def listMemo(from: Int = 0, rows: Int = 20): Seq[Memo] =
    DB.withConnection { implicit conn =>
      SQL("""
          select * from memo order by id
          limit {rows} offset {from}
          """
      ).on(
        'rows -> rows,
        'from -> from
      ).as(simple *)
    }

  def insert(memo: Memo): Option[Long] =
    DB.withConnection { implicit conn =>
      SQL("""
          insert into memo values (
            (select next value for memo_seq),
            {memo}, default, {category_id}
          )""")
        .on('memo -> memo.memo)
        .on('category_id -> memo.categoryId)
        .executeInsert()
    }
}
