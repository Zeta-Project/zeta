package controllers

import models.{MetaModel, MetaModelDatabase, SecureSocialUser}
import modigen.util.MetamodelBuilder
import play.api.libs.json.{JsObject, Json}
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Await
import scala.concurrent.duration.Duration


class MetaModelRESTAPI(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser]{

  private def getModel(modelId: String) : Option[MetaModel] =
   Await.result(MetaModelDatabase.loadModel(modelId), Duration.create(5, "seconds"))

  def getmClasses(modelId: String) = SecuredAction { implicit request =>
     getModel(modelId) match {
       case Some(model) =>
         Ok(MetamodelBuilder().fromJson(Json.parse(model.model).asInstanceOf[JsObject])
           .classes.keys.mkString("",", ",""))
       case _ => BadRequest("Model For Id Does Not Exist!")
     }
  }

  def getmRefs(modelId: String) = SecuredAction { implicit request =>
    getModel(modelId) match {
      case Some(model) =>
        Ok(MetamodelBuilder().fromJson(Json.parse(model.model).asInstanceOf[JsObject])
          .references.keys.mkString("",", ",""))
      case _ => BadRequest("Model For Id Does Not Exist!")
    }
  }

  def getMetaModel(metamodelId: String) = SecuredAction { implicit request =>
    BadRequest("currently not implemented")
  }

  def getmRef(mrefId: String) = SecuredAction { implicit request =>
    BadRequest("currently not implemented")
  }

  def getmClass(mclassId: String) = SecuredAction { implicit request =>
    BadRequest("currently not implemented")
  }

}
