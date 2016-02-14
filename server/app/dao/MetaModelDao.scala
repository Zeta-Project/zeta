package dao

import models.metaModel._
import models.metaModel.mCore.{MReference, MClass}
import play.api.libs.json._
import reactivemongo.api.commands.WriteResult

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

  // must be a def!
  def metaModels = collection("mmd_new")

  val idProjection = Json.obj("_id" -> 0)

  def insert(m: MetaModel): Future[WriteResult] = {
    val ins = if(m.id != None) m else m.copy(id = Some(java.util.UUID.randomUUID().toString))
    metaModels.insert(ins)
  }

  def get(id: String): Future[Option[MetaModel]] = {
    val query = Json.obj("id" -> id)
    metaModels.find(query).projection(idProjection).one[MetaModel]
  }

  def getAsJson(id: String): Future[Option[JsValue]] = {
    val query = Json.obj("_id" -> id)
    metaModels.find(query).projection(idProjection).one[JsValue]
  }

  def getDefinition(id: String): Future[Option[Definition]] = {
    get(id).map(_.map(_.definition))
  }

  def getMClasses(id: String): Future[Option[Definition]] = {
    getDefinition(id).map(_.map(d => d.copy(mObjects = d.mObjects.filter(t => t._2.isInstanceOf[MClass]))))
  }

  def getMReferences(id: String): Future[Option[Definition]] = {
    getDefinition(id).map(_.map(d => d.copy(mObjects = d.mObjects.filter(t => t._2.isInstanceOf[MReference]))))
  }

  def getMClass(id: String, mClassName: String): Future[Option[Definition]] = {
    getDefinition(id).map(_.map(d => d.copy(mObjects = d.mObjects.filterKeys(_ == mClassName))))
  }

  def getMReference(id: String, mReferenceName: String): Future[Option[Definition]] = {
    getDefinition(id).map(_.map(d => d.copy(mObjects = d.mObjects.filterKeys(_ == mReferenceName))))
  }

  def getShape(id: String): Future[Option[Shape]] = {
    get(id).map(_.map(_.shape))
  }

  def getStyle(id: String): Future[Option[Style]] = {
    get(id).map(_.map(_.style))
  }

  def getDiagram(id: String): Future[Option[Diagram]] = {
    get(id).map(_.map(_.diagram))
  }


}