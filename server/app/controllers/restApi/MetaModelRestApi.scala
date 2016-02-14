package controllers.restApi

import javax.inject.Inject

import dao.MetaModelDao
import models.metaModel._
import models.metaModel.MetaModel._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, BodyParsers}
import util.definitions.UserEnvironment


class MetaModelRestApi @Inject()(override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {

  def insert = Action(BodyParsers.parse.json) { request =>
    val result = request.body.validate[MetaModel]
    result.fold(
      errors => {
        BadRequest(JsError.toFlatJson(errors))
      },
      metaModel => {
        MetaModelDao.insert(metaModel)
        Created
      }
    )
  }

  // inserts whole metamodel structure (mcore, dsls..) just by receiving mcore without dsls, not a fan of this..
  def alternativeInsert = Action(BodyParsers.parse.json) { request =>
    val result = request.body.validate[Definition]
    result.fold(
      errors => {
        BadRequest(JsError.toFlatJson(errors))
      },
      definition => {
        val metaModel = MetaModel(None, "", definition, Style(""), Shape(""), Diagram(""))
        MetaModelDao.insert(metaModel)
        Created
      }
    )
  }

  def getMetaModel(id: String) = Action.async { implicit request =>
    MetaModelDao.get(id).map {
      case Some(metaModel) => Ok(Json.toJson(metaModel))
      case None => NotFound
    }
  }

  // "coast-to-coast"
  def getMetaModelAsJson(id: String) = Action.async { implicit request =>
    MetaModelDao.getAsJson(id).map {
      case Some(json) => Ok(json)
      case None => NotFound
    }
  }

  def getMetaModelDefinition(id: String) = Action.async { implicit request =>
    MetaModelDao.getDefinition(id).map {
      case Some(definition) => Ok(Json.toJson(definition))
      case None => NotFound
    }
  }

  def getStyle(id: String) = Action.async { implicit request =>
    MetaModelDao.getStyle(id).map {
      case Some(style) => Ok(Json.toJson(style))
      case None => NotFound
    }
  }

  def getShape(id: String) = Action.async { implicit request =>
    MetaModelDao.getShape(id).map {
      case Some(shape) => Ok(Json.toJson(shape))
      case None => NotFound
    }
  }

  def getDiagram(id: String) = Action.async { implicit request =>
    MetaModelDao.getDiagram(id).map {
      case Some(diagram) => Ok(Json.toJson(diagram))
      case None => NotFound
    }
  }

  def getMClasses(id: String) = Action.async { implicit request =>
    MetaModelDao.getMClasses(id).map {
      case Some(definition) => Ok(Json.toJson(definition))
      case None => NotFound
    }
  }

  def getMReferences(id: String) = Action.async { implicit request =>
    MetaModelDao.getMReferences(id).map {
      case Some(definition) => Ok(Json.toJson(definition))
      case None => NotFound
    }
  }

  def getMClass(id: String, name: String) = Action.async { implicit request =>
    MetaModelDao.getMClass(id, name).map {
      case Some(definition) => Ok(Json.toJson(definition))
      case None => NotFound
    }
  }

  def getMReference(id: String, name: String) = Action.async { implicit request =>
    MetaModelDao.getMReference(id, name).map {
      case Some(definition) => Ok(Json.toJson(definition))
      case None => NotFound
    }
  }


}