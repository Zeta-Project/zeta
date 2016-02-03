package dao

import models.metaModel.mCore.MCoreWrites._
import models.metaModel.mCore.MCoreReads._
import models.metaModel.mCore.MetaModelDefinition

import play.api.libs.json._

import reactivemongo.bson.BSONObjectID

import scala.concurrent.Future

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import play.modules.reactivemongo.json._

import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection._

object MetaModelDao {

  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]

  def collection(name: String): JSONCollection =
    reactiveMongoApi.db.collection[JSONCollection](name)

  def saveMetaModelDefinition(mmd: MetaModelDefinition): Unit = {
    val obj = Json.obj("name" -> "test", "value" -> Json.toJson(mmd))
    collection("mmd").insert(mmd).map(lastError =>
      println("Mongo LastError: %s".format(lastError)))
  }

  def getMetaModelDefinitionJson(id: String): Future[Option[JsValue]] = {
    val query = Json.obj("_id" -> BSONObjectID(id))
    val projection = Json.obj("_id" -> 0)
    collection("mmd").find(query).projection(projection).one[JsValue]
  }

  def getMetaModelDefinition(id: String): Future[Option[MetaModelDefinition]] = {
    val query = Json.obj("_id" -> BSONObjectID(id))
    val projection = Json.obj("_id" -> 0)
    collection("mmd").find(query).projection(projection).one[MetaModelDefinition]
  }
}