package dao.model

import java.time.Instant
import javax.inject.Inject

import dao.metaModel.ZetaMetaModelDao
import dao.{ModelsWriteResult, ReactiveMongoHelper, DbWriteResult, GenericDocumentDao}
import models.modelDefinitions.model.{ModelShortInfo, ModelEntity}
import play.modules.reactivemongo.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import scala.concurrent.Future

trait ZetaModelDao extends GenericDocumentDao[ModelEntity, String] {
  def hasAccess(metaModelId: String, userId: String): Future[Option[Boolean]]

  def findModelsByUser(userId: String): Future[Seq[ModelShortInfo]]
}

class ModelDao @Inject()(metaModelDao: ZetaMetaModelDao) extends ZetaModelDao with ReactiveMongoHelper[String] {

  // yes, should be a def
  def models = collection("md_new")

  override def findModelsByUser(userId: String): Future[Seq[ModelShortInfo]] = {
    val query = Json.obj("userId" -> userId)
    models.find(query).cursor[ModelShortInfo].collect[List]()
  }

  override def update(selector: JsObject, modifier: JsObject): Future[ModelsWriteResult] = {
    models.update(selector, modifier).map {
      res => wrapUpdateResult(res)
    }
  }

  override def find(query: JsObject): Future[Seq[ModelEntity]] = ???

  override def find(query: JsObject, projection: JsObject): Future[Seq[JsValue]] = {
    models.find(query).cursor[JsValue].collect[List]()
  }

  override def update(entity: ModelEntity): Future[ModelsWriteResult] = {
    val modifier = Json.obj("$set" -> Json.obj(
      "model" -> entity.model,
      "updated" -> Instant.now
    ))
    models.update(Json.obj("id" -> entity.id), modifier).map {
      res => wrapUpdateResult(res)
    }
  }

  override def insert(entity: ModelEntity): Future[ModelsWriteResult] = {
    models.insert(entity).map {
      res => {
        val r = wrapWriteResult(res, Some(entity.id))
        if (r.ok) r.copy(insertId = Some(entity.id)) else r
      }
    }
  }

  override def findById(id: String): Future[Option[ModelEntity]] = {
    findOne(Json.obj("id" -> id))
  }

  override def deleteById(id: String): Future[ModelsWriteResult] = {
    models.remove(Json.obj("id" -> id)).map {
      res => wrapWriteResult(res, None)
    }
  }

  override def hasAccess(id: String, userId: String): Future[Option[Boolean]] = {
    findOne(Json.obj("id" -> id)).map {
      case Some(metaModel) => Some(metaModel.userId == userId)
      case _ => None
    }
  }

  override def findOne(query: JsObject): Future[Option[ModelEntity]] = {
    val jsValueModel = findOneAsJsValue(query)
    val optMetaModelEntity = jsValueModel.map { optModel =>
      optModel.flatMap(jsVal => extactMetaModelId(jsVal))
    }.flatMap {
      case None => Future.successful(None)
      case Some(id) => metaModelDao.findById(id)
    }
    for (
      optJsVal <- jsValueModel;
      optEntity <- optMetaModelEntity
    ) yield {
      optEntity.flatMap(me => optJsVal.map(v => {
        val x = v.validate[ModelEntity](ModelEntity.reads(me.id, me.metaModel))
        x.get
      }))
    }
  }

  def findOneAsJsValue(query: JsObject): Future[Option[JsValue]] = {
    models.find(query).one[JsValue]
  }

  def extactMetaModelId(model: JsValue): Option[String] = {
    (model \ "metaModelId").asOpt[String]
  }

}
