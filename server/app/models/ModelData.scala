package models

import com.novus.salat._
import com.mongodb.casbah.Imports._
import com.novus.salat.annotations._
import com.novus.salat.dao.{ModelCompanion, SalatDAO}
import com.novus.salat.transformers.CustomTransformer
import models.oauth.MongoInstance
import models.{MongoDbUserService, SecureSocialUser}
import org.joda.time.DateTime
import models.oauth.custom_context._
import play.api.libs.json.{Json, JsValue}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future


case class Model(
  @Key("_id") id: ObjectId,
  creatorId: Option[String],
  createdAt: Option[DateTime],
  modelData: DBObject
) {
  lazy val modelDataJson = Json.parse(com.mongodb.util.JSON.serialize(modelData))
}

object Model extends ModelCompanion[Model, ObjectId] {

  def apply(id: ObjectId, creatorId: Option[String], createdAt: Option[DateTime], modelData: JsValue): Model = {
    val dbObj = com.mongodb.util.JSON.parse(Json.stringify(modelData)).asInstanceOf[DBObject]
    Model(id, creatorId, createdAt, dbObj)
  }

  val collection = MongoInstance("models")
  override val dao = new SalatDAO[Model, ObjectId](collection = collection) {}

  def findById(id: String): Future[Option[Model]] = Future { findOne(MongoDBObject("_id" -> new ObjectId(id))) }

  def getEdges(id: String): Future[Option[Model]] = Future { findElements(id, "mRef") }

  def getNodes(id: String): Future[Option[Model]] = Future { findElements(id, "mClass")}

  def getNode(id: String): Future[Option[Model]] = Future { findElement(id, "mClass") }

  def getEdge(id: String): Future[Option[Model]] = Future { findElement(id, "mRef") }

  private def findElement(elementId: String, elementType: String): Option[Model] = {
    val query = "modelData" $elemMatch MongoDBObject("id" -> elementId, "mObj" -> elementType)
    val projection = "modelData" $elemMatch MongoDBObject("id" -> elementId)
    val res = find(query, projection).limit(1)
    if(res.hasNext) Some(res.next) else None
  }

  private def findElements(modelId: String, elementType: String): Option[Model] = {
    val query = List(
      MongoDBObject("$match" -> MongoDBObject("_id" -> new ObjectId(modelId), "modelData.mObj" -> elementType)),
      MongoDBObject("$unwind" -> "$modelData"),
      MongoDBObject("$match" -> MongoDBObject("modelData.mObj" -> elementType)),
      MongoDBObject("$group" -> MongoDBObject("_id" -> "$_id", "modelData" -> MongoDBObject("$push" -> "$modelData")))
    )
    val res = collection.aggregate(query).results.headOption
    res.map { dbo => grater[Model].asObject(dbo) }
  }




}