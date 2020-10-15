package de.htwg.zeta.server.routing.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.language.higherKinds

import com.mohiva.play.silhouette.api.HandlerResult
import play.api.mvc.Request
import play.api.mvc.Result

/**
 */
private[authentication] trait AbstractSilhouetteAction[REQ[_]] extends AbstractAction[REQ] {


  protected[authentication] def executeCheckedHandlerResult(block: () => Future[Result], ec: ExecutionContext): Future[HandlerResult[Nothing]] = {
    executeChecked(block).map(HandlerResult[Nothing](_))(ec)
  }

  protected[authentication] def handleSilhouetteRequest[C](
      block: (REQ[C]) => Future[Result],
      ec: ExecutionContext): (Request[C]) => Future[HandlerResult[Nothing]]


  override protected[authentication] def handleFutureRequest[C](block: (REQ[C]) => Future[Result], ec: ExecutionContext)
    (request: Request[C]): Future[Result] = {
    handleSilhouetteRequest(block, ec)(request).map(_.result)(ec)

  }
}
