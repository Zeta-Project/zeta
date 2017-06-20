package de.htwg.zeta.server.model.codeEditor

import java.util.UUID

import com.mongodb.ServerAddress
import com.mongodb.casbah.Imports.DBObject
import com.mongodb.casbah.Imports.wrapDBObj
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.TypeImports
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat
import com.novus.salat.Context
import com.novus.salat.StringTypeHintStrategy
import com.novus.salat.TypeHintFrequency
import play.api.Logger
import play.api.Play

/** Represents a Serverside CodeDocument which is stored in the Database */
case class DbCodeDocument(docId: UUID, dslType: String, metaModelId: UUID, doc: scalot.Server)

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

  def saveDocument(doc: DbCodeDocument): TypeImports.WriteResult = {
    coll.update(MongoDBObject("docId" -> doc.docId), doc, upsert = true)
  }

  def getDocWithIdAndDslType(metaModelId: UUID, dslType: String): Option[DbCodeDocument] =
    coll.find((x: DBObject) => x.metaModelId == metaModelId && x.dslType == dslType) match {
      case Some(doc) => Some(salat.grater[DbCodeDocument].asObject(doc))
      case _ => None
    }

  def getAllDocuments: Seq[DbCodeDocument] = coll
    .find(MongoDBObject())
    .map(DBObj2Doc).toSeq

  def deleteDocWithId(docId: UUID) = coll.remove(MongoDBObject("docId" -> docId))

  /** Implicit Salat Conversions */
  implicit def Doc2DBObj(u: DbCodeDocument): DBObject = salat.grater[DbCodeDocument].asDBObject(u)

  implicit def DBObj2Doc(obj: DBObject): DbCodeDocument = salat.grater[DbCodeDocument].asObject(obj)

  implicit def MDBObj2Doc(obj: MongoDBObject): DbCodeDocument = salat.grater[DbCodeDocument].asObject(obj)

}
