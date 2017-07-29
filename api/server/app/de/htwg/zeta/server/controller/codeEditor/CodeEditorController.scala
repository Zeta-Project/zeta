package de.htwg.zeta.server.controller.codeEditor

import java.util.UUID
import javax.inject.Inject

import akka.actor.ActorRef
import akka.actor.Props
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.server.model.codeEditor.CodeDocWsActor
import de.htwg.zeta.server.model.codeEditor.CodeDocManagerContainer
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Result

class CodeEditorController @Inject()(codeDocManager: CodeDocManagerContainer) extends Controller {

  def codeEditor(metaModelId: UUID, dslType: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Ok(views.html.metamodel.MetaModelCodeEditor(Some(request.identity), metaModelId, dslType))
  }


  def codeSocket(metaModelId: UUID, dslType: String)(securedRequest: SecuredRequest[ZetaEnv, AnyContent], out: ActorRef): Props = {
    CodeDocWsActor.props(out, codeDocManager.manager, metaModelId, dslType)
  }

  def methodCodeEditor(metaModelId: UUID, methodName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Ok(views.html.methodCodeEditor.MethodCodeEditor(Some(request.identity), metaModelId, methodName))
  }
}

