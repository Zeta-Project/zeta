package models.metaModel

import com.mongodb.casbah._
import com.mongodb.casbah.commons._
import com.mongodb.{DBObject, ServerAddress}
import com.novus.salat._
import play.api.{Logger, Play}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/** Represents a metamodel */

case class MetaModel_2(uuid: String, userUuid: String, metaModel: MetaModelData_2, style: MetaModelStyle_2, shape: MetaModelShape_2, diagram: MetaModelDiagram_2)

case class MetaModelData_2(name: String, data: String, graph: String)

case class MetaModelStyle_2(code: String = "")

case class MetaModelShape_2(code: String = "")

case class MetaModelDiagram_2(code: String = "")

object MetaModelDatabase_2 {
  val log = Logger(this getClass() getName())

  val mongoClient = MongoClient(new ServerAddress(Play.current.configuration.getString("mongodb.ip").get))
  val db = mongoClient(Play.current.configuration.getString("mongodb.name").get)
  val coll = db("MetaModels")

  /** Salat Context **/
  implicit val ctx = new Context {
    val name = "MetaModelCtx"
  }
  ctx.registerClassLoader(Play.classloader(Play.current))

  /** upserts the metamodel in the database */
  def saveModel(model: MetaModel_2) = Future(
    coll.update(
      MongoDBObject("uuid" -> model.uuid),
      grater[MetaModel_2].asDBObject(model),
      upsert = true,
      concern = WriteConcern.Acknowledged)
  )

  /** loads Model with uuid */
  def loadModel(uuid: String): Future[Option[MetaModel_2]] = Future {
    coll.find(MongoDBObject("uuid" -> uuid)).next() match {
      case x: DBObject => Some(grater[MetaModel_2].asObject(new MongoDBObject(x)))
      case _ => None
    }
  }

  def modelExists(uuid: String): Future[Boolean] = Future {
    coll.findOne(MongoDBObject("uuid" -> uuid)) match {
      case Some(_) => true
      case None => false
    }
  }

  /** returns all models created by [[models.SecureSocialUser]] with uuid ==  userUuid */
  def modelsOfUser(userUuid: String): Future[List[MetaModel_2]] = Future {
    coll
      .find(MongoDBObject("userUuid" -> userUuid))
      .map(x => grater[MetaModel_2].asObject(new MongoDBObject(x))).toList
  }

  def deleteModel(uuid: String) = Future {
    coll.remove(MongoDBObject("uuid" -> uuid))
  }

  def updateCode(dslType: String, metaModelUuid: String, code: String) = Future {
    coll.update(
      MongoDBObject("uuid" -> metaModelUuid),
      MongoDBObject(
        "$set" -> MongoDBObject(
          dslType + ".code" -> code
        )
      )
    )
  }

  def updateMetaModelData(metaModelUuid: String, metaModelData: MetaModelData_2) = Future {
    coll.update(
      MongoDBObject("uuid" -> metaModelUuid),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "metaModel" -> grater[MetaModelData_2].asDBObject(metaModelData)
        )
      )
    )
  }

}

