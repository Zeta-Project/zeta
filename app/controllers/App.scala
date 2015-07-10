package controllers

import java.util.UUID

import models._
import play.api.Logger
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

  def saveMetaModel() = SecuredAction { implicit request =>
    request.body.asJson match {
      case None =>
        BadRequest("No Body Supplied")
      case Some(json) =>
        MetaModelDatabase.saveModel(
          new MetaModel(
            model = (json \ "data").as[String],
            name = (json \ "name").as[String],
            uuid = UUID.randomUUID().toString,
            userUuid = request.user.uuid.toString
          ))
        Ok("Success.")
    }
  }
}
