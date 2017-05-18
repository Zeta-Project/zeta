package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.Silhouette
import de.htwg.zeta.server.authentication.BasicAction
import de.htwg.zeta.server.authentication.UnAuthenticatedAction
import de.htwg.zeta.server.authentication.AuthenticatedAction
import de.htwg.zeta.server.authentication.BasicWebSocket
import de.htwg.zeta.server.authentication.AuthenticatedWebSocket
import de.htwg.zeta.server.authentication.UnAuthenticatedWebSocket
import play.api.i18n.MessagesApi
import play.api.mvc.Controller
import utils.auth.ZetaEnv

/**
 * All routes are managed in this class
 */
class ScalaRoutes @Inject()(messagesApi: MessagesApi, silhouette: Silhouette[ZetaEnv], system: ActorSystem, mat: Materializer) extends Controller {

  private object AuthenticatedGet extends AuthenticatedAction(messagesApi, silhouette)

  private object AuthenticatedPost extends AuthenticatedAction(messagesApi, silhouette)

  private object AuthenticatedSocket extends AuthenticatedWebSocket(system, silhouette, mat)


  private object UnAuthenticatedGet extends UnAuthenticatedAction(messagesApi, silhouette)

  private object UnAuthenticatedPost extends UnAuthenticatedAction(messagesApi, silhouette)

  private object UnAuthenticatedSocket extends UnAuthenticatedWebSocket(system, silhouette, mat)


  private object BasicGet extends BasicAction(messagesApi, silhouette)

  private object BasicPost extends BasicAction(messagesApi, silhouette)

  private object BasicSocket extends BasicWebSocket(system, silhouette, mat)




}
