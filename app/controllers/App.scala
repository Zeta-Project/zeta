package controllers

import java.util.UUID

import models._
import play.api.Logger
import play.api.libs.json.JsValue
import securesocial.core.RuntimeEnvironment

class App(override implicit val env: RuntimeEnvironment[SecureSocialUser])

  extends securesocial.core.SecureSocial[SecureSocialUser] {

  val log = Logger(this getClass() getName())

  def index() = SecuredAction { implicit request =>
    Ok(views.html.index.render(Some(request.user)))
  }

  def metaModelEditor() = SecuredAction { implicit request =>
    Ok(views.html.metaModelEditor.render())
  }

  def modelValidator() = SecuredAction { implicit request =>
    Ok(views.html.modelValidator.render())
  }

  def saveMetaModel() = SecuredAction { implicit request =>
    println(request.body.toString)
    request.body.asJson match{
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
