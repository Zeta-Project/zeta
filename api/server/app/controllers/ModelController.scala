package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import models.document.ModelEntity
import models.document.Repository
import models.model.ModelWsActor
import play.api.Logger
import play.api.libs.streams.ActorFlow
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.WebSocket
import utils.auth.DefaultEnv
import utils.auth.RepositoryFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ModelController @Inject() (
    implicit mat: Materializer,
    system: ActorSystem,
    repositoryFactory: RepositoryFactory,
    silhouette: Silhouette[DefaultEnv])
  extends Controller {

  val log = Logger(this getClass () getName ())

  def repository[A]()(implicit request: SecuredRequest[DefaultEnv, A]): Repository =
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
