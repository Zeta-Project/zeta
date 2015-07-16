package models

import com.mongodb.{DBObject, ServerAddress}
import com.mongodb.casbah._
import com.mongodb.casbah.commons._
import com.novus.salat._
import play.api.{Play, Logger}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** Represents a metamodel*/
case class MetaModel(model: String, name: String, uuid: String, userUuid: String)

object MetaModelDatabase {
  val log = Logger(this getClass() getName())

  val mongoClient = MongoClient(new ServerAddress(AppConfig.mongoDbIp))
  val db = mongoClient(AppConfig.mongoDbName)
  val coll = db("MetaModels")

  /** Salat Context **/
  implicit val ctx =  new Context{
    val name ="MetaModelCtx"
  }
  ctx.registerClassLoader(Play.classloader(Play.current))

  /** upserts the metamodel in the database */
  def saveModel(model: MetaModel) = Future(
    coll.update(
      MongoDBObject("uuid" -> model.uuid),
      grater[MetaModel].asDBObject(model),
      upsert = true,
      concern = WriteConcern.Acknowledged)
  )

  /** loads Model with uuid */
  def loadModel(uuid: String) : Future[Option[MetaModel]] = Future{
      coll.find(MongoDBObject("uuid" -> uuid)).next() match {
        case x: DBObject  => Some(grater[MetaModel].asObject(new MongoDBObject(x)))
        case _ => None
      }
  }

  /** returns all models created by [[models.SecureSocialUser]] with uuid ==  userUuid */
  def modelsOfUser(userUuid: String) : Future[List[MetaModel]] = Future{
    coll
      .find(MongoDBObject("userUuid" -> userUuid))
      .map(x =>  grater[MetaModel].asObject(new MongoDBObject(x))).toList
  }
}

