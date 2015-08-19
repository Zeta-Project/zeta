package models

import com.mongodb.ServerAddress
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat.Context
import play.api.{Play, Logger}
import com.novus.salat._

/** Represents a Serverside CodeDocument which is stored in the Database */
case class DBCodeDocument(docId: String,
                          diagramId: String,
                          doc: scalot.Server)

/**
 * Code Database Responsible for persisting and retrieving the CodeDocuments.
 * Each CodeDocument
 */
object CodeDocumentDB {
  val log = Logger(this getClass() getName())

  val mongoClient = MongoClient(new ServerAddress(Play.current.configuration.getString("mongodb.ip").get))
  val db = mongoClient(Play.current.configuration.getString("mongodb.name").get)
  val coll = db("CodeDocuments")

  /** Salat Context **/
  implicit val ctx = new Context {
    val name = "CodeDocCtx"
    override val typeHintStrategy = StringTypeHintStrategy(when = TypeHintFrequency.Always, typeHint = "_typeHint")
  }
  ctx.registerClassLoader(Play.classloader(Play.current))

  def saveDocument(doc: DBCodeDocument) = coll.update(MongoDBObject("docId" -> doc.docId), doc, upsert = true)


  def getDocWithId(id: String): Option[DBCodeDocument] = coll.find((x: DBObject) => x.docId == id) match {
    case Some(doc) => Some(grater[DBCodeDocument].asObject(doc))
    case _ => None
  }

  def getDocsWithDiagramId(diagramId: String): Seq[DBCodeDocument] =
    coll.find(MongoDBObject("diagramId" -> diagramId)).map(DBObj2Doc).toSeq

  def getAllDocuments: Seq[DBCodeDocument] = coll
    .find(MongoDBObject())
    .map(DBObj2Doc).toSeq

  def deleteDocWithId(docId: String) = coll.remove(MongoDBObject("docId" -> docId))

  /** Implicit Salat Conversions */
  implicit def Doc2DBObj(u: DBCodeDocument): DBObject = grater[DBCodeDocument].asDBObject(u)

  implicit def DBObj2Doc(obj: DBObject): DBCodeDocument = grater[DBCodeDocument].asObject(obj)

  implicit def MDBObj2Doc(obj: MongoDBObject): DBCodeDocument = grater[DBCodeDocument].asObject(obj)
}
