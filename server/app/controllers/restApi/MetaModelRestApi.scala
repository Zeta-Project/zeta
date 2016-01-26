package controllers.restApi

import javax.inject.Inject

import models.metaModel.mCore.MetaModelDefinition
import models.metaModel.mCore.MCoreReads._
import models.metaModel.mCore.MCoreWrites._
import models.metaModel.{MetaModel, MetaModelDatabase}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, BodyParsers}
import util.definitions.UserEnvironment

import scala.concurrent.Await
import scala.concurrent.duration.Duration


class MetaModelRestApi @Inject()(override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {

  private def getModel(modelId: String): Option[MetaModel] =
    Await.result(MetaModelDatabase.loadModel(modelId), Duration.create(5, "seconds"))

  def getmClasses(modelId: String) = SecuredAction { implicit request =>
    getModel(modelId) match {
      case Some(model) =>
        /* Ok(MetamodelBuilder().fromJson(Json.parse(model.model).asInstanceOf[JsObject])
           .classes.keys.mkString("", ", ", ""))*/
        Ok("")
      case _ => BadRequest("Model For Id Does Not Exist!")
    }
  }

  def getmRefs(modelId: String) = SecuredAction { implicit request =>
    getModel(modelId) match {
      case Some(model) =>
        /*Ok(MetamodelBuilder().fromJson(Json.parse(model.model).asInstanceOf[JsObject])
          .references.keys.mkString("", ", ", ""))*/
        Ok("")
      case _ => BadRequest("Model For Id Does Not Exist!")
    }
  }


  def getMetaModel(metamodelId: String) = Action(BodyParsers.parse.json) { request =>
    Ok("ok")
    /*
    request.body.asJson.map { json =>
      json.validate[Test].map {
        case (name, age) => Ok("Hello " + name + ", you're " + age)
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
    */
  }

  def getmRef(mrefId: String) = SecuredAction { implicit request =>
    BadRequest("currently not implemented")
  }

  def getmClass(mclassId: String) = SecuredAction { implicit request =>
    BadRequest("currently not implemented")
  }

  // just for testing purposes, creates an mcore metamodel based on incoming json
  def mcore = Action(BodyParsers.parse.json) { request =>
    val result = request.body.validate[MetaModelDefinition]
    result.fold(
      errors => {
        BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toFlatJson(errors)))
      },
      mm => {
        //Ok(Json.obj("status" ->"OK", "content" -> Json.toJson(mm)))
        Ok(Json.toJson(mm))
      }
    )
  }

}