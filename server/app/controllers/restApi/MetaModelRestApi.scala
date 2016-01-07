package controllers.restApi

import models.SecureSocialUser
import models.metaModel.{MetaModel, MetaModelDatabase}
import play.api.mvc.{Action, BodyParsers}
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Await
import scala.concurrent.duration.Duration


class MetaModelRestApi(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser] {

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

}