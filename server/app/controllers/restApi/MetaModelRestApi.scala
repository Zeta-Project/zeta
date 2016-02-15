package controllers.restApi

import javax.inject.Inject
import dao.metaModel.MetaModelDaoImpl
import models.metaModel._
import models.metaModel.MetaModel._
import models.metaModel.mCore.{MReference, MClass}
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
        MetaModelDaoImpl.save(metaModel)
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
        MetaModelDaoImpl.save(metaModel)
        Created
      }
    )
  }

  def deleteMetaModel(id: String) = Action.async { implicit request =>
    MetaModelDaoImpl.deleteById(id).map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getMetaModel(id: String) = Action.async { implicit request =>
    MetaModelDaoImpl.findById(id).map {
      case Some(metaModel) => Ok(Json.toJson(metaModel))
      case None => NotFound
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
    MetaModelDaoImpl.findById(id).map {
      case Some(metaModel) => Ok(Json.toJson(metaModel.definition))
      case None => NotFound
    }
  }

  def getStyle(id: String) = Action.async { implicit request =>
    MetaModelDaoImpl.findById(id).map {
      case Some(metaModel) => Ok(Json.toJson(metaModel.style))
      case None => NotFound
    }
  }

  def getShape(id: String) = Action.async { implicit request =>
    MetaModelDaoImpl.findById(id).map {
      case Some(metaModel) => Ok(Json.toJson(metaModel.shape))
      case None => NotFound
    }
  }

  def getDiagram(id: String) = Action.async { implicit request =>
    MetaModelDaoImpl.findById(id).map {
      case Some(metaModel) => Ok(Json.toJson(metaModel.diagram))
      case None => NotFound
    }
  }

  def getMClasses(id: String) = Action.async { implicit request =>
    MetaModelDaoImpl.findById(id).map {
      case Some(metaModel) => {
        val d = metaModel.definition
        val classesDef = d.copy(mObjects = d.mObjects.filter(t => t._2.isInstanceOf[MClass]))
        Ok(Json.toJson(classesDef))
      }
      case None => NotFound
    }
  }

  def getMReferences(id: String) = Action.async { implicit request =>
    MetaModelDaoImpl.findById(id).map {
      case Some(metaModel) => {
        val d = metaModel.definition
        val refsDef = d.copy(mObjects = d.mObjects.filter(t => t._2.isInstanceOf[MReference]))
        Ok(Json.toJson(refsDef))
      }
      case None => NotFound
    }
  }

  def getMClass(id: String, name: String) = Action.async { implicit request =>
    MetaModelDaoImpl.findById(id).map {
      case Some(metaModel) => {
        val d = metaModel.definition
        val classDef = d.copy(mObjects = d.mObjects.filter(p => p._1 == name && p._2.isInstanceOf[MClass]))
        Ok(Json.toJson(classDef))
      }
      case None => NotFound
    }
  }

  def getMReference(id: String, name: String) = Action.async { implicit request =>
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