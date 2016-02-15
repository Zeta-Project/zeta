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
  def deleteById(id: String): Future[Result]
  def save(entity: T): Future[Result]
}

trait ReactiveMongoHelper {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  def collection(name: String): JSONCollection =
    reactiveMongoApi.db.collection[JSONCollection](name)

  def wrapUpdateResult(updateWriteResult: UpdateWriteResult) = {
    Result(
      updateWriteResult.ok,
      updateWriteResult.errmsg
    )
  }

  def wrapWriteResult(writeResult: WriteResult) = {
    Result(
      writeResult.ok,
      writeResult.errmsg
    )
  }
}

case class Result(ok: Boolean, errorMessage: Option[String])
object Result {
  implicit val resultWrites = Json.writes[Result]
}
