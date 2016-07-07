package controllers.restApi

import java.time.Instant
import javax.inject.Inject
import dao.metaModel._
import dao.model.ZetaModelDao
import dao.DbWriteResult
import models.modelDefinitions.helper.HLink
import models.modelDefinitions.model.elements.{Edge, Node}
import models.modelDefinitions.model.{ModelEntity, Model}
import models.oAuth.OAuthDataHandler
import play.api.libs.json._
import play.api.mvc._
import models.modelDefinitions.model.elements.ModelWrites._
import scala.concurrent.Future
import scalaoauth2.provider.OAuth2Provider
import scalaoauth2.provider.OAuth2ProviderActionBuilders._

/**
  * RESTful API for model definitions
  *
  * @param metaModelDao the metamodel DAO (usually injected)
  * @param modelDao the model DAO (usually injected)
  */
class ModelRestApi @Inject()(metaModelDao: ZetaMetaModelDao, modelDao: ZetaModelDao) extends Controller with OAuth2Provider {

  /** Lists all models for the requesting user, provides HATEOAS links */
  def showForUser = Action.async { implicit request =>
    oAuth { userId =>
      modelDao.findModelsByUser(userId).map { res =>
        val out = res.map { info => info.copy(links = Some(Seq(
          HLink.get("self", routes.ModelRestApi.get(info.id).absoluteURL),
          HLink.get("meta_model", routes.MetaModelRestApi.get(info.metaModelId).absoluteURL),
          HLink.delete("remove", routes.ModelRestApi.get(info.id).absoluteURL)
        )))
        }
        Ok(Json.toJson(out))
      }
    }
  }

  /** inserts whole model structure */
  def insert = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      (request.body \ "metaModelId").validate[String].fold(
        error => Future.successful(BadRequest(JsError.toJson(error))),
        metaModelId => validateAndInsert(request.body, userId, metaModelId)
      )
    }
  }

  /** helper method for model insert */
  private def validateAndInsert(jsModel: JsValue, userId: String, metaModelId: String): Future[Result] = {
    metaModelDao.findById(metaModelId) flatMap {
      case Some(metaModelEntity) if metaModelEntity.userId == userId => {
        jsModel.validate[ModelEntity](ModelEntity.strippedReads(metaModelId, metaModelEntity.metaModel)) match {
          case JsSuccess(entity, _) => {
            val modelEntity = entity.asNew(userId, metaModelId)
            modelDao.insert(modelEntity).map(res => Created(Json.toJson(res)))
          }
          case e: JsError => Future.successful(BadRequest(JsError.toJson(e)))
        }
      }
      case None => Future.successful(NotFound(s"Metamodel with id $metaModelId was not found"))
      case _ => Future.successful(Unauthorized)
    }
  }

  /** updates whole model structure */
  def update(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      modelDao.findById(id).flatMap {
        case Some(modelEntity) if modelEntity.userId == userId => {
          request.body.validate[ModelEntity](ModelEntity.strippedReads(modelEntity.metaModelId, modelEntity.model.metaModel)) match {
            case JsSuccess(entity, _) => modelDao.update(entity.asUpdate(modelEntity.id)).map(res => Ok(Json.toJson(res)))
            case e: JsError => Future.successful(BadRequest(JsError.toJson(e)))
          }
        }
        case None => Future.successful(NotFound)
        case _ => Future.successful(Unauthorized)
      }
    }
  }

  /** updates model definition only */
  def updateModel(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      modelDao.findById(id).flatMap {
        case Some(modelEntity) if modelEntity.userId == userId => {
          request.body.validate[Model](Model.reads(modelEntity.model.metaModel)) match {
            case JsSuccess(model, _) => {
              val selector = Json.obj("id" -> id)
              val modifier = Json.obj("$set" -> Json.obj("model" -> model, "updated" -> Instant.now))
              modelDao.update(selector, modifier).map(res => Ok(Json.toJson(res)))
            }
            case e: JsError => Future.successful(BadRequest(JsError.toJson(e)))
          }
        }
        case None => Future.successful(NotFound)
        case _ => Future.successful(Unauthorized)
      }
    }
  }

  /** returns whole model structure incl. HATEOS links */
  def get(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: ModelEntity) => {
        val out = m.copy(links = Some(Seq(
          HLink.put("update", routes.ModelRestApi.get(m.id).absoluteURL),
          HLink.get("meta_model", routes.MetaModelRestApi.get(m.metaModelId).absoluteURL),
          HLink.delete("remove", routes.ModelRestApi.get(m.id).absoluteURL)
        )))
        Ok(Json.toJson(out)(ModelEntity.strippedWrites))
      })
    }
  }

  /** returns model definition only */
  def getModelDefinition(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: ModelEntity) => Ok(Json.toJson(m.model)))
    }
  }

  /** returns all nodes of a model as json array */
  def getNodes(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: ModelEntity) => {
        val d = m.model
        val reduced = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[Node]))
        Ok(Json.toJson(reduced.elements.values))
      })
    }
  }

  /** returns specific node of a specific model as json object */
  def getNode(id: String, name: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: ModelEntity) => {
        val d = m.model
        val reduced = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[Node]))
        reduced.elements.values.headOption.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
      })
    }
  }

  /** returns all edges of a model as json array */
  def getEdges(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: ModelEntity) => {
        val d = m.model
        val reduced = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[Edge]))
        Ok(Json.toJson(reduced.elements.values))
      })
    }
  }

  /** returns specific edge of a specific model as json object */
  def getEdge(id: String, name: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: ModelEntity) => {
        val d = m.model
        val reduced = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[Edge]))
        reduced.elements.values.headOption.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
      })
    }
  }

  /** deletes a whole model */
  def delete(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedWrite(id, {
        modelDao.deleteById(id)
      })
    }
  }

  /** A helper method for less verbose oauth auth check */
  def oAuth[A](block: String => Future[Result])(implicit request: Request[A]) = {
    authorize(OAuthDataHandler()) { authInfo => block(authInfo.user.uuid.toString) }
  }

  /** A helper method for less verbose reads from the database */
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

  /** A helper method for less verbose writes to the database */
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