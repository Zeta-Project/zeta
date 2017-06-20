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
import de.htwg.zeta.common.models.entity.CodeDocument
import play.api.Logger
import play.api.Play

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

  def saveDocument(doc: CodeDocument): TypeImports.WriteResult = {
    coll.update(MongoDBObject("docId" -> doc.id), doc, upsert = true)
  }

  def getDocWithIdAndDslType(metaModelId: UUID, dslType: String): Option[CodeDocument] =
    coll.find((x: DBObject) => x.metaModelId == metaModelId && x.dslType == dslType) match {
      case Some(doc) => Some(salat.grater[CodeDocument].asObject(doc))
      case _ => None
    }

  def getAllDocuments: Seq[CodeDocument] = coll
    .find(MongoDBObject())
    .map(DBObj2Doc).toSeq

  def deleteDocWithId(docId: UUID) = coll.remove(MongoDBObject("docId" -> docId))

  /** Implicit Salat Conversions */
  implicit def Doc2DBObj(u: CodeDocument): DBObject = salat.grater[CodeDocument].asDBObject(u)

  implicit def DBObj2Doc(obj: DBObject): CodeDocument = salat.grater[CodeDocument].asObject(obj)

  implicit def MDBObj2Doc(obj: MongoDBObject): CodeDocument = salat.grater[CodeDocument].asObject(obj)

}
