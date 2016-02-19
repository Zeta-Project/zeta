package dao.metaModel

import dao.{DbWriteResult, GenericMongoDao, ReactiveMongoHelper}
import models.metaModel._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.modules.reactivemongo.json._
import scala.concurrent.Future


trait MetaModelDao extends GenericMongoDao[MetaModel] with ReactiveMongoHelper {
  def findIdsByUser(userId: String): Future[Seq[MetaModelShortInfo]]

  def hasAccess(id: String, userId: String): Future[Option[Boolean]]

  def updateDefinition(metaModelId: String, definition: Definition): Future[DbWriteResult]

  // "Coast to Coast" (read-only)
}

object MetaModelDaoImpl extends MetaModelDao {

  // should be a def! => better for connection pooling
  def metaModels = collection("mmd_new")

  val idProjection = Json.obj("_id" -> 0)

  override def findIdsByUser(userId: String): Future[Seq[MetaModelShortInfo]] = {
    val query = Json.obj("userId" -> userId)
    metaModels.find(query).cursor[MetaModel].collect[List]().map {
      _.map { s =>
        MetaModelShortInfo(s.id, s.definition.name)
      }
    }
  }

  override def findById(id: String): Future[Option[MetaModel]] = {
    findOne(Json.obj("id" -> id))
  }

  override def findOne(query: JsObject): Future[Option[MetaModel]] = {
    metaModels.find(query).projection(idProjection).one[MetaModel]
  }

  override def find(query: JsObject): Future[Seq[MetaModel]] = {
    metaModels.find(query).cursor[MetaModel].collect[List]()
  }

  override def deleteById(id: String): Future[DbWriteResult] = {
    metaModels.remove(Json.obj("id" -> id)).map {
      res => wrapWriteResult(res)
    }
  }

  override def insert(entity: MetaModel): Future[DbWriteResult] = {
    metaModels.insert(entity).map {
      res => {
        val r = wrapWriteResult(res)
        if(r.ok) r.copy(insertId = Some(entity.id)) else r
      }
    }
  }

  override def update(entity: MetaModel): Future[DbWriteResult] = {
    val modifier = Json.obj("$set" -> entity)
    metaModels.update(Json.obj("id" -> entity.id), modifier).map {
      res => wrapUpdateResult(res)
    }
  }

  override def update(selector: JsObject, modifier: JsObject): Future[DbWriteResult] = {
    metaModels.update(selector, modifier).map {
      res => wrapUpdateResult(res)
    }
  }

  override def updateDefinition(metaModelId: String, definition: Definition): Future[DbWriteResult] = {
    val selector = Json.obj("id" -> metaModelId)
    val modifier = Json.obj("$set" -> Json.obj("definition" -> definition))
    update(selector, modifier)
  }

  override def hasAccess(id: String, userId: String): Future[Option[Boolean]] = {
    findOne(Json.obj("id" -> id)).map {
      case Some(m) => Some(m.userId == userId)
      case _ => None
    }
  }

}