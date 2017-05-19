package de.htwg.zeta.server.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.Result
import de.htwg.zeta.server.util.auth.ZetaEnv

/**
 */
private[authentication] abstract class AbstractSilhouetteAction[REQ <: Request[AnyContent]](
    messagesApi: MessagesApi, silhouette: Silhouette[ZetaEnv]
) extends AbstractAction[REQ](messagesApi, silhouette) {


  protected[authentication] def executeCheckedHandlerResult(block: () => Future[Result], ec: ExecutionContext): Future[HandlerResult[Nothing]] = {
    executeChecked(block).map(HandlerResult[Nothing](_))(ec)
  }

  protected[authentication] def handleSilhouetteRequest(
    block: (REQ) => Future[Result],
    ec: ExecutionContext): (Request[AnyContent]) => Future[HandlerResult[Nothing]]

  override protected[authentication] def handleFutureRequest(block: (REQ) => Future[Result], ec: ExecutionContext)
    (request: Request[AnyContent]): Future[Result] = {
    handleSilhouetteRequest(block, ec)(request).map(_.result)(ec)
  }

}
