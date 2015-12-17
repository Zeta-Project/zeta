package controllers

import models.{CodeDocWsActor, CodeDocManagingActor, SecureSocialUser}
import play.api.Play.current
import play.api.mvc.WebSocket
import securesocial.core.RuntimeEnvironment


class CodeEditorController(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser] {

  def codeSocket(metaModelUuid: String, dslType: String) = WebSocket.acceptWithActor[String, String] { request => out =>
    CodeDocWsActor.props(out, CodeDocManagingActor.getCodeDocManager, metaModelUuid, dslType)
  }

}
