package de.htwg.zeta.server.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.actions.SecuredRequestHandlerBuilder
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.i18n.MessagesApi
import play.api.mvc.AnyContent
import play.api.mvc.Request
import play.api.mvc.Result

/**
 */
class AuthenticatedAction(messagesApi: MessagesApi, silhouette: Silhouette[ZetaEnv], auth: Option[Authorization[ZetaEnv#I, ZetaEnv#A]] = None)
  extends AbstractSilhouetteAction[SecuredRequest[ZetaEnv, AnyContent]](messagesApi, silhouette) {

  private val builder: SecuredRequestHandlerBuilder[ZetaEnv] = {
    val default: SecuredRequestHandlerBuilder[ZetaEnv] = silhouette.SecuredRequestHandler
    default.copy[ZetaEnv](authorization = auth)
  }

  override protected[authentication] def handleSilhouetteRequest(
    block: (SecuredRequest[ZetaEnv, AnyContent]) => Future[Result],
    ec: ExecutionContext): (Request[AnyContent]) => Future[HandlerResult[Nothing]] = {
    def ret(request: Request[AnyContent]): Future[HandlerResult[Nothing]] = {
      builder(request)((req: SecuredRequest[ZetaEnv, AnyContent]) => {
        executeCheckedHandlerResult(() => block(req), ec)
      })
    }

    ret
  }
}
