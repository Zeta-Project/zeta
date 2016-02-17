package dao

import play.api.Play._
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.commands.{WriteResult, UpdateWriteResult}

import scala.concurrent.Future

trait GenericMongoDao[T] {
  def findById(id: String): Future[Option[T]]
  def findOne(query: JsObject): Future[Option[T]]
  def find(query: JsObject): Future[Seq[T]]
  def deleteById(id: String): Future[DbWriteResult]
  def insert(entity: T): Future[DbWriteResult]
  def update(entity: T): Future[DbWriteResult]
  def update(selector: JsObject, modifier: JsObject): Future[DbWriteResult]
}

trait ReactiveMongoHelper {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  def collection(name: String): JSONCollection =
    reactiveMongoApi.db.collection[JSONCollection](name)

  def wrapUpdateResult(updateWriteResult: UpdateWriteResult) = {
    DbWriteResult(
      updateWriteResult.ok,
      updateWriteResult.errmsg
    )
  }

  def wrapWriteResult(writeResult: WriteResult) = {
    DbWriteResult(
      writeResult.ok,
      writeResult.errmsg
    )
  }
}

case class DbWriteResult(ok: Boolean, errorMessage: Option[String])
object DbWriteResult {
  implicit val resultWrites = Json.writes[DbWriteResult]
}
