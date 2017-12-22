package de.htwg.zeta.server.routing.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Silhouette
import de.htwg.zeta.server.silhouette.ZetaEnv
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.api.mvc.Result


/**
 */
class BasicAction(messagesApi: MessagesApi, silhouette: Silhouette[ZetaEnv]) extends AbstractAction[Request](messagesApi, silhouette) {

  override protected[authentication] def handleFutureRequest[C](block: (Request[C]) => Future[Result], ec: ExecutionContext)
    (request: Request[C]): Future[Result] = {
    executeChecked(() => block(request))
  }

  override protected[authentication] def reqToRequest[C](r: Request[C]): Request[C] = r
}
