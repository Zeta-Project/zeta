package de.htwg.zeta.server.authentication

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
  protected val messagesApi: MessagesApi
  protected val silhouette: Silhouette[ZetaEnv]
  protected val system: ActorSystem
  protected val mat: Materializer


  protected object AuthenticatedGet extends AuthenticatedAction(messagesApi, silhouette)

  protected object AuthenticatedPost extends AuthenticatedAction(messagesApi, silhouette)

  protected object AuthenticatedPut extends AuthenticatedAction(messagesApi, silhouette)

  protected object AuthenticatedDelete extends AuthenticatedAction(messagesApi, silhouette)

  protected object AuthenticatedSocket extends AuthenticatedWebSocket(system, silhouette, mat)

  private lazy val authorization: Option[Authorization[ZetaEnv#I, ZetaEnv#A]] = Some(WithProvider[ZetaEnv#A](CredentialsProvider.ID))

  protected object AuthenticatedWithProviderGet extends AuthenticatedAction(messagesApi, silhouette, authorization)

  protected object AuthenticatedWithProviderPost extends AuthenticatedAction(messagesApi, silhouette, authorization)


  protected object UnAuthenticatedGet extends UnAuthenticatedAction(messagesApi, silhouette)

  protected object UnAuthenticatedPost extends UnAuthenticatedAction(messagesApi, silhouette)

  protected object UnAuthenticatedSocket extends UnAuthenticatedWebSocket(system, silhouette, mat)


  protected object BasicGet extends BasicAction(messagesApi, silhouette)

  protected object BasicPost extends BasicAction(messagesApi, silhouette)

  protected object BasicSocket extends BasicWebSocket(system, silhouette, mat)


}
