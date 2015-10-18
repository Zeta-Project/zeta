package controllers

import java.util.UUID

import models.{MetaModel, MetaModelDatabase, SecureSocialUser}
import play.api.libs.json.JsValue
import securesocial.core.RuntimeEnvironment

/**
 * Created by mgt on 17.10.15.
 */
class MetaModelController(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser] {

  def codeEditor() = SecuredAction { implicit request =>
    //println(request.body.asFormUrlEncoded.get.get("test").head)
    Ok(views.html.metamodel.MetaModelCodeEditor.render(Some(request.user)))
  }

  def metaModelEditor() = SecuredAction { implicit request =>
    Ok(views.html.metamodel.MetaModelGraphicalEditor.render(Some(request.user)))
  }

  def modelValidator() = SecuredAction { implicit request =>
    Ok(views.html.model.modelValidator.render())
  }

  def saveMetaModel() = SecuredAction { implicit request =>
    println(request.body.toString)
    request.body.asJson match {
      case Some(json) =>
        MetaModelDatabase.saveModel(new MetaModel(
          model = (json \ "data").as[JsValue].toString(),
          name = (json \ "name").as[String],
          uuid = UUID.randomUUID().toString,
          userUuid = request.user.uuid.toString))
        Ok("Saved.")
      case _ =>
        BadRequest("No valid Json Supplied.")
    }
  }

}
