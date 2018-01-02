package de.htwg.zeta.server.routing.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import play.api.mvc.Request
import play.api.mvc.Result


/**
 */
class BasicAction(override val dependencies: AbstractAction.Dependencies) extends AbstractAction[Request] {

  override protected[authentication] def handleFutureRequest[C](block: (Request[C]) => Future[Result], ec: ExecutionContext)
    (request: Request[C]): Future[Result] = {
    executeChecked(() => block(request))
  }

  override protected[authentication] def reqToRequest[C](r: Request[C]): Request[C] = r
}
