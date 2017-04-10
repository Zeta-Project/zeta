package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import models.document.MetaModelEntity
import models.document.Repository
import models.metaModel._
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.WebSocket
import utils.auth.DefaultEnv
import utils.auth.RepositoryFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by mgt on 17.10.15.
 */

class MetaModelController @Inject() (
    implicit mat: Materializer,
    system: ActorSystem,
    repositoryFactory: RepositoryFactory,
    silhouette: Silhouette[DefaultEnv])
  extends Controller {

  def repository[A]()(implicit request: SecuredRequest[DefaultEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  def metaModelEditor(metaModelUuid: String) = silhouette.SecuredAction.async { implicit request =>
    repository.get[MetaModelEntity](metaModelUuid).map { metaModelEntity =>
      Ok(views.html.metamodel.MetaModelGraphicalEditor(Some(request.identity), metaModelUuid, metaModelEntity))
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def metaModelSocket(metaModelUuid: String) = WebSocket.acceptOrResult[JsValue, JsValue] { request =>
    implicit val req = Request(request, AnyContentAsEmpty)
    silhouette.SecuredRequestHandler { securedRequest =>
      Future.successful(HandlerResult(Ok, Some(securedRequest.identity)))
    }.map {
      case HandlerResult(r, Some(user)) => Right(ActorFlow.actorRef(out => MetaModelWsActor.props(out, metaModelUuid)))
      case HandlerResult(r, None) => Left(r)
    }
  }
}
