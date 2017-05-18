package de.htwg.zeta.server.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.MessagesApi
import play.api.mvc.Result
import play.api.mvc.Request
import play.api.mvc.AnyContent
import utils.auth.ZetaEnv

/**
 */
class UnAuthenticatedAction(
    messagesApi: MessagesApi, silhouette: Silhouette[ZetaEnv]
) extends AbstractSilhouetteAction[Request[AnyContent]](messagesApi, silhouette) {


  override protected[authentication] def handleSilhouetteRequest(
      block: (Request[AnyContent]) => Future[Result],
      ec: ExecutionContext): (Request[AnyContent]) => Future[HandlerResult[Nothing]] = {
    (request: Request[AnyContent]) =>
      silhouette.UnsecuredRequestHandler(request)(req => {
        executeCheckedHandlerResult(() => block(req), ec)
      })
  }

}
