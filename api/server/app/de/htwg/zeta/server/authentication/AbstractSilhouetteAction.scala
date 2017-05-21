package de.htwg.zeta.server.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.HandlerResult
import com.mohiva.play.silhouette.api.Silhouette
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.api.mvc.Result

/**
 */
private[authentication] abstract class AbstractSilhouetteAction[REQ[_]](
    messagesApi: MessagesApi, silhouette: Silhouette[ZetaEnv]
) extends AbstractAction[REQ](messagesApi, silhouette) {


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
