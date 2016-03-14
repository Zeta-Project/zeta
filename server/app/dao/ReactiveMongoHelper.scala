package dao

import play.api.Play._
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}

trait ReactiveMongoHelper[P] {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  def collection(name: String): JSONCollection =
    reactiveMongoApi.db.collection[JSONCollection](name)

  def wrapUpdateResult(updateWriteResult: UpdateWriteResult) = {
    ModelsWriteResult(
      updateWriteResult.ok,
      updateWriteResult.nModified,
      updateWriteResult.errmsg,
      None
    )
  }

  def wrapWriteResult(writeResult: WriteResult, id: Option[String]) = {
    ModelsWriteResult(
      writeResult.ok,
      writeResult.n,
      writeResult.errmsg,
      id
    )
  }
}

case class ModelsWriteResult(
  ok: Boolean,
  affected: Int,
  errorMessage: Option[String],
  insertId: Option[String]
) extends dao.DbWriteResult[String]
