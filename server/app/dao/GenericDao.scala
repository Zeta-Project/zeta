package dao

import play.api.libs.json._

import scala.concurrent.Future

trait GenericDao[E,P] {
  def findById(id: P): Future[Option[E]]
  def insert(entity: E): Future[DbWriteResult[P]]
  def update(entity: E): Future[DbWriteResult[P]]
  def deleteById(id: P): Future[DbWriteResult[P]]
}

trait GenericDocumentDao[E,P] extends GenericDao[E,P] {
  def findOne(query: JsObject): Future[Option[E]]
  def find(query: JsObject): Future[Seq[E]]
  def find(query: JsObject, projection: JsObject): Future[Seq[JsValue]]
  def update(selector: JsObject, modifier: JsObject): Future[DbWriteResult[P]]
}

trait DbWriteResult[P]{
  val ok: Boolean
  val affected: Int
  val errorMessage: Option[String]
  val insertId: Option[P]
}

object DbWriteResult {
  implicit def resWrites[T](implicit nested: Writes[Option[T]]): Writes[DbWriteResult[T]] = OWrites[DbWriteResult[T]] { res =>
    Json.obj("ok" -> res.ok, "affected" -> res.affected, "errorMessage" -> res.errorMessage,
      "insertId" -> nested.writes(res.insertId))
  }
}


