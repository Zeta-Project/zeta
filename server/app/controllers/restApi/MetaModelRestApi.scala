package controllers.restApi

import dao.metaModel.MetaModelDaoImpl
import models.metaModel._
import models.metaModel.MetaModel._
import models.metaModel.mCore.{MReference, MClass}
import models.oAuth.OAuthDataHandler
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Controller, Action, BodyParsers}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scalaoauth2.provider.OAuth2Provider


class MetaModelRestApi extends Controller with OAuth2Provider {

  def showForUser = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      MetaModelDaoImpl.findIdsByUser(authInfo.user.uuid.toString).map { res =>
        Ok(Json.toJson(res))
      }
    }
  }

  def insert = Action.async(BodyParsers.parse.json) { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      val result = request.body.validate[MetaModel]
      result.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        metaModel => {
          val in = metaModel.copy(
            id = Some(java.util.UUID.randomUUID().toString),
            userId = authInfo.user.uuid.toString
          )
          MetaModelDaoImpl.insert(in).map { res =>
            Created(Json.obj("id" -> in.id))
          }

        }
      )
    }
  }

  def update(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      val result = request.body.validate[MetaModel]
      result.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        metaModel => {
          val in = metaModel.copy(id = Some(id), userId = authInfo.user.uuid.toString)
          MetaModelDaoImpl.update(in).map { res =>
            Ok
          }
        }
      )
    }
  }

  // inserts whole metamodel structure (mcore, dsls..) just by receiving mcore without dsls, not a fan of this..
  def alternativeInsert = Action.async(BodyParsers.parse.json) { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      val result = request.body.validate[Definition]
      result.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        definition => {
          val in = MetaModel(
            Some(java.util.UUID.randomUUID().toString),
            authInfo.user.uuid.toString,
            definition, Style(""),
            Shape(""),
            Diagram("")
          )
          MetaModelDaoImpl.insert(in).map { res =>
            Created(Json.obj("id" -> in.id))
          }
        }
      )
    }
  }

  def deleteMetaModel(id: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      MetaModelDaoImpl.deleteById(id).map { res =>
        Ok(Json.toJson(res))
      }
    }
  }

  def getMetaModel(id: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      MetaModelDaoImpl.findById(id).map {
        case Some(metaModel) => Ok(Json.toJson(metaModel))
        case None => NotFound
      }
    }
  }

  // "coast-to-coast"
  //  def getMetaModelAsJson(id: String) = Action.async { implicit request =>
  //    MetaModelDaoImpl.getAsJson(id).map {
  //      case Some(json) => Ok(json)
  //      case None => NotFound
  //    }
  //  }

  def getMetaModelDefinition(id: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      MetaModelDaoImpl.findById(id).map {
        case Some(metaModel) => Ok(Json.toJson(metaModel.definition))
        case None => NotFound
      }
    }
  }

  def getStyle(id: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      MetaModelDaoImpl.findById(id).map {
        case Some(metaModel) => Ok(Json.toJson(metaModel.style))
        case None => NotFound
      }
    }
  }

  def getShape(id: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      MetaModelDaoImpl.findById(id).map {
        case Some(metaModel) => Ok(Json.toJson(metaModel.shape))
        case None => NotFound
      }
    }
  }

  def getDiagram(id: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      MetaModelDaoImpl.findById(id).map {
        case Some(metaModel) => Ok(Json.toJson(metaModel.diagram))
        case None => NotFound
      }
    }
  }

  def getMClasses(id: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      MetaModelDaoImpl.findById(id).map {
        case Some(metaModel) => {
          val d = metaModel.definition
          val classesDef = d.copy(mObjects = d.mObjects.filter(t => t._2.isInstanceOf[MClass]))
          Ok(Json.toJson(classesDef))
        }
        case None => NotFound
      }
    }
  }

  def getMReferences(id: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      MetaModelDaoImpl.findById(id).map {
        case Some(metaModel) => {
          val d = metaModel.definition
          val refsDef = d.copy(mObjects = d.mObjects.filter(t => t._2.isInstanceOf[MReference]))
          Ok(Json.toJson(refsDef))
        }
        case None => NotFound
      }
    }
  }

  def getMClass(id: String, name: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      MetaModelDaoImpl.findById(id).map {
        case Some(metaModel) => {
          val d = metaModel.definition
          val classDef = d.copy(mObjects = d.mObjects.filter(p => p._1 == name && p._2.isInstanceOf[MClass]))
          Ok(Json.toJson(classDef))
        }
        case None => NotFound
      }
    }
  }

  def getMReference(id: String, name: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      MetaModelDaoImpl.findById(id).map {
        case Some(metaModel) => {
          val d = metaModel.definition
          val refDef = d.copy(mObjects = d.mObjects.filter(p => p._1 == name && p._2.isInstanceOf[MReference]))
          Ok(Json.toJson(refDef))
        }
        case None => NotFound
      }
    }
  }


}