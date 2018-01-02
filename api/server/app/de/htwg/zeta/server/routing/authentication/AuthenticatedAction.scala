package de.htwg.zeta.server.routing.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.actions.SecuredRequestHandlerBuilder
import de.htwg.zeta.server.silhouette.ZetaEnv
import play.api.mvc.Request
import play.api.mvc.Result

object AuthenticatedAction {
  private[AuthenticatedAction] type R[C] = SecuredRequest[ZetaEnv, C]
}

class AuthenticatedAction(
    override val dependencies: AbstractAction.Dependencies,
    auth: Option[Authorization[ZetaEnv#I, ZetaEnv#A]] = None)
  extends AbstractSilhouetteAction[AuthenticatedAction.type#R] {


  private val builder: SecuredRequestHandlerBuilder[ZetaEnv] = {
    val default: SecuredRequestHandlerBuilder[ZetaEnv] = dependencies.silhouette.SecuredRequestHandler
    default.copy[ZetaEnv](authorization = auth)
  }

  override protected[authentication] def handleSilhouetteRequest[C](
      block: (SecuredRequest[ZetaEnv, C]) => Future[Result],
      ec: ExecutionContext
  ): (Request[C]) => Future[HandlerResult[Nothing]] = {
    def ret(request: Request[C]): Future[HandlerResult[Nothing]] = {
      builder(request)((req: SecuredRequest[ZetaEnv, C]) => {
        executeCheckedHandlerResult(() => block(req), ec)
      })
    }

    ret
  }

  override protected[authentication] def reqToRequest[C](r: SecuredRequest[ZetaEnv, C]): Request[C] = r
}
