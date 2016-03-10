package dao.metaModel

import java.time.Instant


import dao._
import models.modelDefinitions.metaModel.{MetaModelShortInfo, MetaModelEntity, MetaModel}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.modules.reactivemongo.json._
import scala.concurrent.Future

trait ZetaMetaModelDao extends GenericDocumentDao[MetaModelEntity, String] {
  def hasAccess(metaModelId: String, userId: String): Future[Option[Boolean]]
  def findMetaModelsByUser(userId: String): Future[Seq[MetaModelShortInfo]]
}

object MetaModelDao extends ZetaMetaModelDao with ReactiveMongoHelper[String] {

  // yes, should be a def
  def metaModels = collection("mmd_new")

  override def findById(id: String): Future[Option[MetaModelEntity]] = {
    findOne(Json.obj("id" -> id))
  }

  override def insert(entity: MetaModelEntity): Future[ModelsWriteResult] = {
    metaModels.insert(entity).map {
      res => {
        val r = wrapWriteResult(res, Some(entity.id))
        if (r.ok) r.copy(insertId = Some(entity.id)) else r
      }
    }
  }

  override def update(entity: MetaModelEntity): Future[ModelsWriteResult] = {
    val modifier = Json.obj("$set" -> Json.obj(
      "metaModel" -> entity.metaModel,
      "dsl" -> entity.dsl,
      "updated" -> Instant.now
    ))
    metaModels.update(Json.obj("id" -> entity.id), modifier).map {
      res => wrapUpdateResult(res)
    }
  }

  override def deleteById(id: String): Future[ModelsWriteResult] = {
    metaModels.remove(Json.obj("id" -> id)).map {
      res => wrapWriteResult(res, None)
    }
  }

  override def findOne(query: JsObject): Future[Option[MetaModelEntity]] = {
    metaModels.find(query).one[MetaModelEntity]
  }

  override def find(query: JsObject): Future[Seq[MetaModelEntity]] = {
    metaModels.find(query).cursor[MetaModelEntity].collect[List]()
  }

  override def find(query: JsObject, projection: JsObject): Future[Seq[JsValue]] = {
    metaModels.find(query).cursor[JsValue].collect[List]()
  }

  override def update(selector: JsObject, modifier: JsObject): Future[ModelsWriteResult] = {
    metaModels.update(selector, modifier).map {
      res => wrapUpdateResult(res)
    }
  }

  def updateMetaModel(metaModelId: String, metaModel: MetaModel): Future[ModelsWriteResult] = {
    val selector = Json.obj("id" -> metaModelId)
    val modifier = Json.obj("$set" -> Json.obj("definition" -> metaModel, "updated" -> Instant.now))
    update(selector, modifier)
  }


  override def findMetaModelsByUser(userId: String): Future[Seq[MetaModelShortInfo]] = {
    val query = Json.obj("userId" -> userId)
    metaModels.find(query).cursor[MetaModelEntity].collect[List]().map {
      _.map { s =>
        MetaModelShortInfo(s.id, s.metaModel.name, s.created, s.updated)
      }
    }
  }

  override def hasAccess(id: String, userId: String): Future[Option[Boolean]] = {
    findOne(Json.obj("id" -> id)).map {
      case Some(metaModel) => Some(metaModel.userId == userId)
      case _ => None
    }
  }

}