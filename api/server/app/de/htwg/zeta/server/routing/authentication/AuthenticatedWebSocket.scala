package de.htwg.zeta.server.routing.authentication

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.server.silhouette.ZetaEnv
import play.api.mvc.AnyContent
import play.api.mvc.Request

/**
 */
class AuthenticatedWebSocket(
    override val dependencies: AbstractWebSocket.Dependencies
) extends AbstractWebSocketAPI[SecuredRequest[ZetaEnv, AnyContent]] {

  override protected[authentication] def handleRequest[T](request: Request[AnyContent])
    (buildFlow: (SecuredRequest[ZetaEnv, AnyContent]) => Future[HandlerResult[T]]): Future[HandlerResult[T]] = {
    silhouette.SecuredRequestHandler(buildFlow)(request)
  }
}
