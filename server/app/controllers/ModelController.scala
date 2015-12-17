package controllers

import models.{ModelWsActor, MetaModelDatabase, SecureSocialUser, ShortUuid}
import play.api.Logger
import play.api.Play.current
import play.api.mvc.WebSocket
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Await
import scala.concurrent.duration._

class ModelController(override implicit val env: RuntimeEnvironment[SecureSocialUser]) extends securesocial.core.SecureSocial[SecureSocialUser] {

  val log = Logger(this getClass() getName())

  def newModel(metaModelUuid: String) = SecuredAction { implicit request =>
    log.debug("Calling newModel(" + metaModelUuid + ")")
    if (Await.result(MetaModelDatabase.modelExists(metaModelUuid), 30 seconds)) {
      Redirect(routes.ModelController.modelEditor(metaModelUuid, ShortUuid.uuid))
    } else {
      log.error("attempting to create an editor with unknown uuid")
      Redirect(controllers.webpage.routes.Webpage.index())
    }
  }

  def modelEditor(metaModelUuid: String, modelUuid: String) = SecuredAction { implicit request =>
    Ok(views.html.model.ModelGraphicalEditor.render(metaModelUuid, modelUuid, request.user.uuid.toString, request.user.profile.fullName.getOrElse("")))
  }

  def modelValidator() = SecuredAction { implicit request =>
    Ok(views.html.model.ModelValidator.render())
  }

  def modelSocket(instanceId: String, graphType: String) = WebSocket.acceptWithActor[String, String] { request => out =>
    ModelWsActor.props(out, instanceId, graphType)
  }
}
