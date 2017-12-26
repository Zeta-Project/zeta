package de.htwg.zeta.server.routing.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.HandlerResult
import play.api.mvc.Result
import play.api.mvc.Request

/**
 */
class UnAuthenticatedAction(
    override val dependencies: AbstractAction.Dependencies
) extends AbstractSilhouetteAction[Request] {

  override protected[authentication] def handleSilhouetteRequest[C](
      block: (Request[C]) => Future[Result],
      ec: ExecutionContext
  ): (Request[C]) => Future[HandlerResult[Nothing]] = {
    (request: Request[C]) =>
      dependencies.silhouette.UnsecuredRequestHandler(request)(req => {
        executeCheckedHandlerResult(() => block(req), ec)
      })
  }

  override protected[authentication] def reqToRequest[C](r: Request[C]): Request[C] = r
}
