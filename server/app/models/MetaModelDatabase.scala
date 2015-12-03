package models

import com.mongodb.casbah._
import com.mongodb.casbah.commons._
import com.mongodb.{DBObject, ServerAddress}
import com.novus.salat._
import models.Types.Types
import play.api.{Logger, Play}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}


/** Represents a metamodel */

case class MetaModel(uuid: String, userUuid: String, metaModel: MetaModelData, style: MetaModelStyle, shape: MetaModelShape, diagram: MetaModelDiagram)

case class MetaModelData(name: String, data: String, graph: String)

case class MetaModelStyle(code: String = "")

case class MetaModelShape(code: String = "")

case class MetaModelDiagram(code: String = "")

trait MObject {
  def name: String
}

trait MBounds {
  def upperBound: Int

  def lowerBound: Int
}

trait MSum {}

case class MetaModelDefinition(mClasses: List[MClass], mReferences: List[MReference], mEnums: List[MEnums])

case class MClass(name: String, abstractness: Boolean, superTypes: List[MClass], inputs: List[MLinkDef], outputs: List[MLinkDef], attributes: List[MAttributes]) extends MObject with MSum

case class MReference(name: String, sourceDeletionDeletesTarget: Boolean, targetDeletionDeletesSource: Boolean, source: List[MLinkDef], target: List[MLinkDef]) extends MObject with MSum

case class MAttributes(name: String, globalUnique: Boolean, localUnique: Boolean, default: Types, constant: Boolean, singleAssignment: Boolean, expression: String, mtype: Types, ordered: Boolean, transient: Boolean, upperBound: Int, lowerBound: Int) extends MObject with MBounds

case class MLinkDef(mtype: MSum, deleteIfLower: Boolean, upperBound: Int, lowerBound: Int) extends MBounds

case class MEnums(name: String, values: List[Types]) extends MObject

object Types extends Enumeration {
  type Types = Value
  val Int, String, Double, Boolean, MEnums = Value
}

object MetaModelDatabase {
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
  def saveModel(model: MetaModel) = Future(
    coll.update(
      MongoDBObject("uuid" -> model.uuid),
      grater[MetaModel].asDBObject(model),
      upsert = true,
      concern = WriteConcern.Acknowledged)
  )

  /** loads Model with uuid */
  def loadModel(uuid: String): Future[Option[MetaModel]] = Future {
    coll.find(MongoDBObject("uuid" -> uuid)).next() match {
      case x: DBObject => Some(grater[MetaModel].asObject(new MongoDBObject(x)))
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
  def modelsOfUser(userUuid: String): Future[List[MetaModel]] = Future {
    coll
      .find(MongoDBObject("userUuid" -> userUuid))
      .map(x => grater[MetaModel].asObject(new MongoDBObject(x))).toList
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

  def updateMetaModelData(metaModelUuid: String, metaModelData: MetaModelData) = Future {
    coll.update(
      MongoDBObject("uuid" -> metaModelUuid),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "metaModel" -> grater[MetaModelData].asDBObject(metaModelData)
        )
      )
    )
  }

}

