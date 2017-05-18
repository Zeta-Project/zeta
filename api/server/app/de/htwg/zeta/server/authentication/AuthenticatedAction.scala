package de.htwg.zeta.server.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import play.api.i18n.MessagesApi
import play.api.mvc.AnyContent
import play.api.mvc.Request
import play.api.mvc.Result
import utils.auth.ZetaEnv

/**
 */
class AuthenticatedAction(messagesApi: MessagesApi, silhouette: Silhouette[ZetaEnv])
  extends AbstractSilhouetteAction[SecuredRequest[ZetaEnv, AnyContent]](messagesApi, silhouette) {


  override protected[authentication] def handleSilhouetteRequest(
    block: (SecuredRequest[ZetaEnv, AnyContent]) => Future[Result],
    ec: ExecutionContext): (Request[AnyContent]) => Future[HandlerResult[Nothing]] = {
    def ret(request: Request[AnyContent]): Future[HandlerResult[Nothing]] =
      silhouette.SecuredRequestHandler(request)((req: SecuredRequest[ZetaEnv, AnyContent]) => {
        executeCheckedHandlerResult(() => block(req), ec)
      })

    ret
  }
}
