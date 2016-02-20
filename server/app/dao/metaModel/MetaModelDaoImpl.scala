package dao.metaModel

import dao.{DbWriteResult, GenericMongoDao, ReactiveMongoHelper}
import models.metaModel._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.modules.reactivemongo.json._
import scala.concurrent.Future


trait MetaModelDao extends GenericMongoDao[MetaModelEntity] with ReactiveMongoHelper {
  def findIdsByUser(userId: String): Future[Seq[MetaModelShortInfo]]

  def hasAccess(id: String, userId: String): Future[Option[Boolean]]

  def updateDefinition(metaModelId: String, definition: MetaModel): Future[DbWriteResult]

  // "Coast to Coast" (read-only)
}

object MetaModelDaoImpl extends MetaModelDao {

  // should be a def! => better for connection pooling
  def metaModels = collection("mmd_new")

  override def findIdsByUser(userId: String): Future[Seq[MetaModelShortInfo]] = {
    val query = Json.obj("userId" -> userId)
    metaModels.find(query).cursor[MetaModelEntity].collect[List]().map {
      _.map { s =>
        MetaModelShortInfo(s.id, s.definition.name, s.created, s.updated)
      }
    }
  }

  override def findById(id: String): Future[Option[MetaModelEntity]] = {
    findOne(Json.obj("id" -> id))
  }

  override def findOne(query: JsObject): Future[Option[MetaModelEntity]] = {
    metaModels.find(query).one[MetaModelEntity]
  }

  override def find(query: JsObject): Future[Seq[MetaModelEntity]] = {
    metaModels.find(query).cursor[MetaModelEntity].collect[List]()
  }

  override def deleteById(id: String): Future[DbWriteResult] = {
    metaModels.remove(Json.obj("id" -> id)).map {
      res => wrapWriteResult(res)
    }
  }

  override def insert(entity: MetaModelEntity): Future[DbWriteResult] = {
    metaModels.insert(entity).map {
      res => {
        val r = wrapWriteResult(res)
        if(r.ok) r.copy(insertId = Some(entity.id)) else r
      }
    }
  }

  override def update(entity: MetaModelEntity): Future[DbWriteResult] = {
    val modifier = Json.obj("$set" -> entity)
    metaModels.update(Json.obj("id" -> entity.id), modifier).map {
      res => wrapUpdateResult(res)
    }
  }

  override def update(selector: JsObject, modifier: JsObject): Future[DbWriteResult] = {
    metaModels.update(selector, addUpdateTime(modifier)).map {
      res => wrapUpdateResult(res)
    }
  }

  override def updateDefinition(metaModelId: String, definition: MetaModel): Future[DbWriteResult] = {
    val selector = Json.obj("id" -> metaModelId)
    val modifier = Json.obj("$set" -> Json.obj("definition" -> definition))
    update(selector, modifier)
  }

  override def hasAccess(id: String, userId: String): Future[Option[Boolean]] = {
    findOne(Json.obj("id" -> id)).map {
      case Some(metaModel) => Some(metaModel.userId == userId)
      case _ => None
    }
  }

  private def addUpdateTime(update: JsObject) = {
    Json.obj("$currentDate" -> Json.obj("updated" -> true)) ++ update
  }

}