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
import play.api.libs.streams.ActorFlow
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.WebSocket
import play.api.mvc.AnyContent
import play.api.mvc.Result

class ModelController @Inject()(
    implicit mat: Materializer,
    system: ActorSystem,
    repositoryFactory: RepositoryFactory,
    silhouette: Silhouette[ZetaEnv])
  extends Controller {

  private def repository[A](request: SecuredRequest[ZetaEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  def modelEditor(metaModelUuid: String, modelUuid: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    repository(request).get[ModelEntity](modelUuid).map { model =>
      Ok(views.html.model.ModelGraphicalEditor(model.metaModelId, modelUuid, Some(request.identity), model))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def vrModelEditor(metaModelUuid: String, modelUuid: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    repository(request).get[ModelEntity](modelUuid).map { _ =>
      Ok(views.html.VrEditor(metaModelUuid))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def modelValidator(request: SecuredRequest[ZetaEnv, AnyContent]): Result = {
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
