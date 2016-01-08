package controllers

import javax.inject.Inject

import models.SecureSocialUser
import models.codeEditor.{CodeDocWsActor, CodeDocManagingActor}
import play.api.Play.current
import play.api.mvc.WebSocket
import securesocial.core.RuntimeEnvironment
import util.definitions.UserEnvironment


class CodeEditorController @Inject() (override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {

  def codeEditor(metaModelUuid: String, dslType: String) = SecuredAction { implicit request =>
    Ok(views.html.metamodel.MetaModelCodeEditor.render(Some(request.user), metaModelUuid, dslType))
  }

  def codeSocket(metaModelUuid: String, dslType: String) = WebSocket.acceptWithActor[String, String] { request => out =>
    CodeDocWsActor.props(out, CodeDocManagingActor.getCodeDocManager, metaModelUuid, dslType)
  }

}
