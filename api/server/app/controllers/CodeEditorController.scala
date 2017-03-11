package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.{ HandlerResult, Silhouette }
import models.codeEditor.{ CodeDocManagingActor, CodeDocWsActor }
import models.document.Repository
import models.document.http.HttpRepository
import play.api.libs.streams.ActorFlow
import play.api.libs.ws.WSClient
import play.api.mvc.{ AnyContentAsEmpty, Controller, Request, WebSocket }
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CodeEditorController @Inject() (implicit mat: Materializer, system: ActorSystem, ws: WSClient, silhouette: Silhouette[DefaultEnv]) extends Controller {

  def codeEditor(metaModelUuid: String, dslType: String) = silhouette.SecuredAction { implicit request =>
    Ok(views.html.metamodel.MetaModelCodeEditor(Some(request.identity), metaModelUuid, dslType))
  }
  val codeDocManager = system.actorOf(CodeDocManagingActor.props())

  def codeSocket(metaModelUuid: String, dslType: String) = WebSocket.acceptOrResult[String, String] { request =>
    implicit val req = Request(request, AnyContentAsEmpty)
    silhouette.SecuredRequestHandler { securedRequest =>
      Future.successful(HandlerResult(Ok, Some(securedRequest.identity)))
    }.map {
      case HandlerResult(r, Some(user)) =>
        val session = request.cookies.get("SyncGatewaySession").get.value
        val repository: Repository = HttpRepository(session)

        Right(ActorFlow.actorRef(out => CodeDocWsActor.props(out, codeDocManager, metaModelUuid, dslType)))
      case HandlerResult(r, None) => Left(r)
    }
  }
}
