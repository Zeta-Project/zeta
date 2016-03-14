package controllers.restApi

import java.time.Instant
import javax.inject.Inject

import dao.metaModel._
import dao.model.ZetaModelDao
import dao.{ModelsWriteResult, DbWriteResult}
import models.modelDefinitions.model.elements.{Edge, Node}
import models.modelDefinitions.model.{ModelEntity, Model}
import models.oAuth.OAuthDataHandler
import play.api.libs.json._
import play.api.mvc._

import ModelsWriteResult._

import models.modelDefinitions.model.elements.ModelWrites._

import scala.concurrent.Future
import scalaoauth2.provider.OAuth2Provider
import scalaoauth2.provider.OAuth2ProviderActionBuilders._


class ModelRestApi @Inject()(metaModelDao: ZetaMetaModelDao, modelDao: ZetaModelDao) extends Controller with OAuth2Provider {

  def showForUser = Action.async { implicit request =>
    oAuth { userId =>
      modelDao.findModelsByUser(userId).map { res =>
        Ok(Json.toJson(res))
      }
    }
  }

  // inserts whole metamodel structure
  def insert = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      (request.body \ "metaModelId").validate[String].fold(
        error => Future.successful(BadRequest(JsError.toFlatJson(error))),
        metaModelId => validateAndInsert(request.body, userId, metaModelId)
      )
    }
  }

  private def validateAndInsert(jsModel: JsValue, userId: String, metaModelId: String): Future[Result] = {
    metaModelDao.findById(metaModelId) flatMap {
      case Some(metaModelEntity) if metaModelEntity.userId == userId => {
        jsModel.validate[ModelEntity](ModelEntity.strippedReads(metaModelId, metaModelEntity.metaModel)) match {
          case JsSuccess(entity, _) => {
            val modelEntity = entity.asNew(userId, metaModelId)
            modelDao.insert(modelEntity).map(res => Created(Json.toJson(res)))
          }
          case e: JsError => Future.successful(BadRequest(JsError.toFlatJson(e)))
        }
      }
      case None => Future.successful(NotFound(s"Metamodel with id $metaModelId was not found"))
      case _ => Future.successful(Unauthorized)
    }
  }

  def update(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      modelDao.findById(id).flatMap {
        case Some(modelEntity) if modelEntity.userId == userId => {
          request.body.validate[ModelEntity](ModelEntity.strippedReads(modelEntity.metaModelId, modelEntity.model.metaModel)) match {
            case JsSuccess(entity, _) => modelDao.update(entity.asUpdate(modelEntity.id)).map(res => Ok(Json.toJson(res)))
            case e: JsError => Future.successful(BadRequest(JsError.toFlatJson(e)))
          }
        }
        case None => Future.successful(NotFound)
        case _ => Future.successful(Unauthorized)
      }
    }
  }

  def updateModel(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      modelDao.findById(id).flatMap {
        case Some(modelEntity) if modelEntity.userId == userId => {
          request.body.validate[Model](Model.reads(modelEntity.model.metaModel)) match {
            case JsSuccess(model, _) =>{
              val selector = Json.obj("id" -> id)
              val modifier = Json.obj("$set" -> Json.obj("model" -> model, "updated" -> Instant.now))
              modelDao.update(selector, modifier).map(res => Ok(Json.toJson(res)))
            }
            case e: JsError => Future.successful(BadRequest(JsError.toFlatJson(e)))
          }
        }
        case None => Future.successful(NotFound)
        case _ => Future.successful(Unauthorized)
      }
    }
  }

  def get(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: ModelEntity) => Ok(Json.toJson(m)(ModelEntity.strippedWrites)))
    }
  }

  def getModelDefinition(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: ModelEntity) => Ok(Json.toJson(m.model)))
    }
  }

  def getNodes(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: ModelEntity) => {
        val d = m.model
        val reduced = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[Node]))
        Ok(Json.toJson(reduced.elements.values))
      })
    }
  }

  def getNode(id: String, name: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: ModelEntity) => {
        val d = m.model
        val reduced = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[Node]))
        reduced.elements.values.headOption.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
      })
    }
  }

  def getEdges(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: ModelEntity) => {
        val d = m.model
        val reduced = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[Edge]))
        Ok(Json.toJson(reduced.elements.values))
      })
    }
  }

  def getEdge(id: String, name: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: ModelEntity) => {
        val d = m.model
        val reduced = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[Edge]))
        reduced.elements.values.headOption.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
      })
    }
  }

  def delete(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedWrite(id, {
        modelDao.deleteById(id)
      })
    }
  }



  def oAuth[A](block: String => Future[Result])(implicit request: Request[A]) = {
    authorize(OAuthDataHandler()) { authInfo => block(authInfo.user.uuid.toString) }
  }

  def protectedRead(id: String, trans: ModelEntity => Result)(implicit userId: String): Future[Result] = {
    modelDao.findById(id).map {
      case Some(model) => if (userId == model.userId) {
        trans(model)
      } else {
        Unauthorized
      }
      case None => NotFound
    }
  }

  private def protectedWrite(id: String, write: => Future[DbWriteResult[String]])(implicit userId: String): Future[Result] = {
    modelDao.hasAccess(id, userId).flatMap {
      case Some(b) => {
        if (b) {
          write.map { res => Ok(Json.toJson(res)) }
        } else {
          Future.successful(Unauthorized)
        }
      }
      case None => Future.successful(NotFound)
    }
  }


}