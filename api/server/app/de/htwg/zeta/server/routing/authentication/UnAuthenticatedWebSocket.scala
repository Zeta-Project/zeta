package de.htwg.zeta.server.routing.authentication

import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import de.htwg.zeta.server.silhouette.ZetaEnv
import play.api.mvc.AnyContent
import play.api.mvc.Request

/**
 */
class UnAuthenticatedWebSocket(
    override val system: ActorSystem,
    override val silhouette: Silhouette[ZetaEnv],
    override val mat: Materializer
) extends AbstractWebSocketAPI[Request[AnyContent]] {

  override protected[authentication] def handleRequest[T](request: Request[AnyContent])
    (buildFlow: (Request[AnyContent]) => Future[HandlerResult[T]]): Future[HandlerResult[T]] = {
    silhouette.UnsecuredRequestHandler(buildFlow)(request)
  }
}
