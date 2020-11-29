package de.htwg.zeta.server.silhouette

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler
import controllers.Assets.Forbidden
import de.htwg.zeta.server.routing.routes
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import play.api.mvc.Results

/**
 * Custom unsecured error handler.
 */
class CustomUnsecuredErrorHandler extends UnsecuredErrorHandler {

  /**
   * Called when a user is authenticated but not authorized.
   *
   * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
   *
   * @param request The request header.
   * @return The result to send to the client.
   */
  override def onNotAuthorized(implicit request: RequestHeader): Future[Result] = {
    Future.successful(Forbidden)
  }
}
