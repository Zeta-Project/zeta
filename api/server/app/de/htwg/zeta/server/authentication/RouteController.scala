package de.htwg.zeta.server.authentication

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import de.htwg.zeta.server.util.auth.ZetaEnv
import de.htwg.zeta.server.util.auth.WithProvider
import play.api.i18n.MessagesApi
import play.api.mvc.Controller

/**
 */
trait RouteController extends Controller {

  protected val routeCont: RouteControllerContainer

  protected object AuthenticatedGet extends AuthenticatedAction(routeCont.messagesApi, routeCont.silhouette)

  protected object AuthenticatedPost extends AuthenticatedAction(routeCont.messagesApi, routeCont.silhouette)

  protected object AuthenticatedPut extends AuthenticatedAction(routeCont.messagesApi, routeCont.silhouette)

  protected object AuthenticatedDelete extends AuthenticatedAction(routeCont.messagesApi, routeCont.silhouette)

  protected object AuthenticatedSocket extends AuthenticatedWebSocket(routeCont.system, routeCont.silhouette, routeCont.mat)


  private lazy val authorization: Option[Authorization[ZetaEnv#I, ZetaEnv#A]] = Some(WithProvider[ZetaEnv#A](CredentialsProvider.ID))

  protected object AuthenticatedWithProviderGet extends AuthenticatedAction(routeCont.messagesApi, routeCont.silhouette, authorization)

  protected object AuthenticatedWithProviderPost extends AuthenticatedAction(routeCont.messagesApi, routeCont.silhouette, authorization)


  protected object UnAuthenticatedGet extends UnAuthenticatedAction(routeCont.messagesApi, routeCont.silhouette)

  protected object UnAuthenticatedPost extends UnAuthenticatedAction(routeCont.messagesApi, routeCont.silhouette)

  protected object UnAuthenticatedSocket extends UnAuthenticatedWebSocket(routeCont.system, routeCont.silhouette, routeCont.mat)


  protected object BasicGet extends BasicAction(routeCont.messagesApi, routeCont.silhouette)

  protected object BasicPost extends BasicAction(routeCont.messagesApi, routeCont.silhouette)

  protected object BasicSocket extends BasicWebSocket(routeCont.system, routeCont.silhouette, routeCont.mat)

}

class RouteControllerContainer @Inject() private(
    val messagesApi: MessagesApi,
    val silhouette: Silhouette[ZetaEnv],
    val system: ActorSystem,
    val mat: Materializer)
