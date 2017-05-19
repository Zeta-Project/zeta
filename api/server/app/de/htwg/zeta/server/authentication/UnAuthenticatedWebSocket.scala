package de.htwg.zeta.server.authentication

import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import play.api.mvc.AnyContent
import play.api.mvc.Request
import de.htwg.zeta.server.util.auth.ZetaEnv

/**
 */
class UnAuthenticatedWebSocket(
    override val system: ActorSystem,
    override val silhouette: Silhouette[ZetaEnv],
    override val mat: Materializer
) extends AbstractWebSocket[Request[AnyContent]] {

  override def handleRequest(request: Request[AnyContent])
    (buildFlow: (Request[AnyContent]) => Future[HandlerResult[Flow[String, String, _]]]): Future[HandlerResult[Flow[String, String, _]]] = {
    silhouette.UnsecuredRequestHandler(buildFlow)(request)
  }
}
