package de.htwg.zeta.server.controller

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import de.htwg.zeta.server.actor.OnlineUserActorFactory
import de.htwg.zeta.server.actor.WebSocketActor
import de.htwg.zeta.server.silhouette.ZetaEnv
import javax.inject.Inject

import play.api.libs.streams.ActorFlow
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.InjectedController
import play.api.mvc.Request
import play.api.mvc.WebSocket

class WebSocketController @Inject()(silhouette: Silhouette[ZetaEnv])(
    implicit system: ActorSystem,
    materializer: Materializer,
    ec: ExecutionContext,
    onlineUserManagerFactory: OnlineUserActorFactory
) extends InjectedController {

  def socket: WebSocket = WebSocket.acceptOrResult[String, String] { request =>
    implicit val req: Request[AnyContentAsEmpty.type] = Request(request, AnyContentAsEmpty)
    silhouette.SecuredRequestHandler { securedRequest =>
      Future.successful(HandlerResult(Ok, Some(securedRequest.identity)))
    }.map {
      case HandlerResult(_, Some(user)) => Right(ActorFlow.actorRef(WebSocketActor.props(user, onlineUserManagerFactory)))
      case HandlerResult(r, None) => Left(r)
    }
  }
}

