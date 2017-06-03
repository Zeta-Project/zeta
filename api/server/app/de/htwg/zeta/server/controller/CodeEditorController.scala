package de.htwg.zeta.server.controller

import java.util.UUID
import javax.inject.Inject

import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.server.model.codeEditor.CodeDocManagingActor
import de.htwg.zeta.server.model.codeEditor.CodeDocWsActor
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.libs.ws.WSClient
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Result

class CodeEditorController @Inject()(implicit mat: Materializer, system: ActorSystem, ws: WSClient, silhouette: Silhouette[ZetaEnv]) extends Controller {

  def codeEditor(metaModelId: UUID, dslType: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
    Ok(views.html.metamodel.MetaModelCodeEditor(Some(request.identity), metaModelId, dslType))
  }

  private val codeDocManager: ActorRef = system.actorOf(CodeDocManagingActor.props())

  def codeSocket(metaModelId: UUID, dslType: String)(securedRequest: SecuredRequest[ZetaEnv, AnyContent], out: ActorRef): Props = {
    CodeDocWsActor.props(out, codeDocManager, metaModelId, dslType)
  }
}
