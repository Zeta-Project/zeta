package de.htwg.zeta.server.authentication

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.MessagesApi
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.Result
import de.htwg.zeta.server.utils.auth.ZetaEnv


/**
 */
class BasicAction(
    messagesApi: MessagesApi, silhouette: Silhouette[ZetaEnv]
) extends AbstractAction[Request[AnyContent]](messagesApi, silhouette) {


  override protected[authentication] def handleFutureRequest(block: (Request[AnyContent]) => Future[Result], ec: ExecutionContext)
    (request: Request[AnyContent]): Future[Result] = {
    executeChecked(() => block(request))
  }

}
