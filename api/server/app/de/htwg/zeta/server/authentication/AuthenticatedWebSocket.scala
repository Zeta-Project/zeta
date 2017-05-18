package de.htwg.zeta.server.authentication

import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorRef
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import grizzled.slf4j.Logging
import play.api.libs.streams.ActorFlow
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.api.mvc.Request
import play.api.mvc.WebSocket
import play.api.mvc.AnyContentAsEmpty
import utils.auth.ZetaEnv

/**
 */
class AuthenticatedWebSocket(
    override val system: ActorSystem,
    override val silhouette: Silhouette[ZetaEnv],
    override val mat: Materializer
) extends AbstractWebSocket[SecuredRequest[ZetaEnv, AnyContent]] {

  override def handleRequest(request: Request[AnyContent])
    (buildFlow: (SecuredRequest[ZetaEnv, AnyContent]) => Future[HandlerResult[Flow[String, String, _]]]): Future[HandlerResult[Flow[String, String, _]]] = {
    silhouette.SecuredRequestHandler(buildFlow)(request)
  }
}
