package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.server.model.model.ModelWsActor
import de.htwg.zeta.server.util.auth.ZetaEnv
import de.htwg.zeta.server.util.auth.RepositoryFactory
import models.document.ModelEntity
import models.document.Repository
import play.api.Logger
import play.api.libs.streams.ActorFlow
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.WebSocket

class ModelController @Inject() (
    implicit mat: Materializer,
    system: ActorSystem,
    repositoryFactory: RepositoryFactory,
    silhouette: Silhouette[ZetaEnv])
  extends Controller {

  val log = Logger(this getClass () getName ())

  def repository[A]()(implicit request: SecuredRequest[ZetaEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  def modelEditor(metaModelUuid: String, modelUuid: String) = silhouette.SecuredAction.async { implicit request =>
    repository.get[ModelEntity](modelUuid).map { model =>
      Ok(views.html.model.ModelGraphicalEditor(model.metaModelId, modelUuid, Some(request.identity), model))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def vrModelEditor(metaModelUuid: String, modelUuid: String) = silhouette.SecuredAction.async { implicit request =>
    repository.get[ModelEntity](modelUuid).map { model =>
      Ok(views.html.VrEditor(metaModelUuid))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def modelValidator() = silhouette.SecuredAction { implicit request =>
    Ok(views.html.model.ModelValidator(Some(request.identity)))
  }

  def modelSocket(instanceId: String, graphType: String) = WebSocket.acceptOrResult[String, String] { request =>
    implicit val req = Request(request, AnyContentAsEmpty)
    silhouette.SecuredRequestHandler { securedRequest =>
      Future.successful(HandlerResult(Ok, Some(securedRequest.identity)))
    }.map {
      case HandlerResult(r, Some(user)) => Right(ActorFlow.actorRef(out => ModelWsActor.props(out, instanceId, graphType)))
      case HandlerResult(r, None) => Left(r)
    }
  }
}
