package controllers

import models.{CodeDocManagingActor, CodeDocWSActor, SecureSocialUser}
import play.api.Play.current
import play.api.mvc.WebSocket
import securesocial.core.RuntimeEnvironment


class CodeEditorController(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser] {

  def codeSocket(metaModelUuid: String, dslType: String) = WebSocket.acceptWithActor[String, String] { request => out =>
    CodeDocWSActor.props(out, CodeDocManagingActor.getCodeDocManager, metaModelUuid, dslType)
  }

}
