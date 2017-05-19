package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.server.model.metaModel.MetaModelWsActor
import de.htwg.zeta.server.utils.auth.ZetaEnv
import de.htwg.zeta.server.utils.auth.RepositoryFactory
import models.document.MetaModelEntity
import models.document.Repository
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.WebSocket

/**
 * Created by mgt on 17.10.15.
 */
class MetaModelController @Inject() (
    implicit mat: Materializer,
    system: ActorSystem,
    repositoryFactory: RepositoryFactory,
    silhouette: Silhouette[ZetaEnv])
  extends Controller {

  def repository[A]()(implicit request: SecuredRequest[ZetaEnv, A]): Repository =
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
