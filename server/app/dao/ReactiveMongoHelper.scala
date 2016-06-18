package dao

import play.api.Play._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}

/**
  * A mixin trait that helps with MongoDB
  * @tparam P the type of the primary key
  */
trait ReactiveMongoHelper[P] {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  def collection(name: String): JSONCollection =
    reactiveMongoApi.db.collection[JSONCollection](name)

  /** wraps MongoDB result in custom result */
  def wrapUpdateResult(updateWriteResult: UpdateWriteResult) = {
    ModelsWriteResult(
      updateWriteResult.ok,
      updateWriteResult.nModified,
      updateWriteResult.errmsg,
      None
    )
  }

  /** wraps MongoDB result in custom result */
  def wrapWriteResult(writeResult: WriteResult, id: Option[String]) = {
    ModelsWriteResult(
      writeResult.ok,
      writeResult.n,
      writeResult.errmsg,
      id
    )
  }
}

/** Just an implementation of DbWriteResult[A] */
case class ModelsWriteResult(
  ok: Boolean,
  affected: Int,
  errorMessage: Option[String],
  insertId: Option[String]
) extends dao.DbWriteResult[String]
