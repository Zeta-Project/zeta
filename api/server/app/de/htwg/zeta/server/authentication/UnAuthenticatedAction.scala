package de.htwg.zeta.server.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.i18n.MessagesApi
import play.api.mvc.Result
import play.api.mvc.Request

/**
 */
class UnAuthenticatedAction(
    messagesApi: MessagesApi, silhouette: Silhouette[ZetaEnv]
) extends AbstractSilhouetteAction[Request](messagesApi, silhouette) {

  override protected[authentication] def handleSilhouetteRequest[C](
    block: (Request[C]) => Future[Result],
    ec: ExecutionContext
  ): (Request[C]) => Future[HandlerResult[Nothing]] = {
    (request: Request[C]) =>
      silhouette.UnsecuredRequestHandler(request)(req => {
        executeCheckedHandlerResult(() => block(req), ec)
      })
  }

  override protected[authentication] def reqToRequest[C](r: Request[C]): Request[C] = r
}
