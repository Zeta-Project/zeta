package controllers

import models.{MetaModel, MetaModelDatabase, SecureSocialUser}
import modigen.util.MetamodelBuilder
import play.api.libs.json.{JsObject, Json}
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Await
import scala.concurrent.duration.Duration


class MetaModelAPI(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser]{

  private def getModel(modelId: String) : Option[MetaModel] =
   Await.result(MetaModelDatabase.loadModel(modelId), Duration.create(5, "seconds"))

  def mClasses(modelId: String) = SecuredAction { implicit request =>
     getModel(modelId) match {
       case Some(model) =>
         Ok(MetamodelBuilder().fromJson(Json.parse(model.metaModel.data).asInstanceOf[JsObject])
           .classes.keys.mkString("",", ",""))
       case _ => BadRequest("Model For Id Does Not Exist!")
     }
  }

  def mRefs(modelId: String) = SecuredAction { implicit request =>
    getModel(modelId) match {
      case Some(model) =>
        Ok(MetamodelBuilder().fromJson(Json.parse(model.metaModel.data).asInstanceOf[JsObject])
          .references.keys.mkString("",", ",""))
      case _ => BadRequest("Model For Id Does Not Exist!")
    }  }
}
