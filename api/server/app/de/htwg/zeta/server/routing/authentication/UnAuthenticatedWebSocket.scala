package de.htwg.zeta.server.routing.authentication

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.HandlerResult
import play.api.mvc.AnyContent
import play.api.mvc.Request

/**
 */
class UnAuthenticatedWebSocket(
    override val dependencies: AbstractWebSocket.Dependencies
) extends AbstractWebSocketAPI[Request[AnyContent]] {

  override protected[authentication] def handleRequest[T](request: Request[AnyContent])
    (buildFlow: (Request[AnyContent]) => Future[HandlerResult[T]]): Future[HandlerResult[T]] = {
    silhouette.UnsecuredRequestHandler(buildFlow)(request)
  }
}
