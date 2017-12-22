package de.htwg.zeta.server.routing.authentication

import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.server.silhouette.ZetaEnv
import play.api.mvc.AnyContent
import play.api.mvc.Request

/**
 */
class AuthenticatedWebSocket(
    override val system: ActorSystem,
    override val silhouette: Silhouette[ZetaEnv],
    override val mat: Materializer
) extends AbstractWebSocketAPI[SecuredRequest[ZetaEnv, AnyContent]] {

  override protected[authentication] def handleRequest[T](request: Request[AnyContent])
    (buildFlow: (SecuredRequest[ZetaEnv, AnyContent]) => Future[HandlerResult[T]]): Future[HandlerResult[T]] = {
    silhouette.SecuredRequestHandler(buildFlow)(request)
  }
}
