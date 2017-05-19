package de.htwg.zeta.server.models.codeEditor

import com.mongodb.ServerAddress
import com.mongodb.casbah.Imports.DBObject
import com.mongodb.casbah.Imports.wrapDBObj
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat
import com.novus.salat.Context
import com.novus.salat.StringTypeHintStrategy
import com.novus.salat.TypeHintFrequency
import play.api.Logger
import play.api.Play

/** Represents a Serverside CodeDocument which is stored in the Database */
case class DbCodeDocument(docId: String, dslType: String, metaModelUuid: String, doc: scalot.Server)

/**
 * Code Database Responsible for persisting and retrieving the CodeDocuments.
 * Each CodeDocument
 */
object CodeDocumentDb {
  val log = Logger(this getClass () getName ())

  val mongoClient = MongoClient(new ServerAddress(Play.current.configuration.getString("mongodb.ip").get))
  val db = mongoClient(Play.current.configuration.getString("mongodb.name").get)
  val coll = db("CodeDocuments")

  /**
   * Salat Context
   */
  implicit val ctx = new Context {
    val name = "CodeDocCtx"
    override val typeHintStrategy = StringTypeHintStrategy(when = TypeHintFrequency.Always, typeHint = "_typeHint")
  }
  ctx.registerClassLoader(Play.classloader(Play.current))

  def saveDocument(doc: DbCodeDocument) = {
    coll.update(MongoDBObject("docId" -> doc.docId), doc, upsert = true)
  }

  def getDocWithId(id: String): Option[DbCodeDocument] = coll.find((x: DBObject) => x.docId == id) match {
    case Some(doc) => Some(salat.grater[DbCodeDocument].asObject(doc))
    case _ => None
  }

  def getDocWithUuidAndDslType(metaModelUuid: String, dslType: String): Option[DbCodeDocument] =
    coll.find((x: DBObject) => x.metaModelUuid == metaModelUuid && x.dslType == dslType) match {
      case Some(doc) => Some(salat.grater[DbCodeDocument].asObject(doc))
      case _ => None
    }

  def getDocsWithDslType(dslType: String): Seq[DbCodeDocument] =
    coll.find(MongoDBObject("dslType" -> dslType)).map(DBObj2Doc).toSeq

  def getAllDocuments: Seq[DbCodeDocument] = coll
    .find(MongoDBObject())
    .map(DBObj2Doc).toSeq

  def deleteDocWithId(docId: String) = coll.remove(MongoDBObject("docId" -> docId))

  /** Implicit Salat Conversions */
  implicit def Doc2DBObj(u: DbCodeDocument): DBObject = salat.grater[DbCodeDocument].asDBObject(u)

  implicit def DBObj2Doc(obj: DBObject): DbCodeDocument = salat.grater[DbCodeDocument].asObject(obj)

  implicit def MDBObj2Doc(obj: MongoDBObject): DbCodeDocument = salat.grater[DbCodeDocument].asObject(obj)
}
