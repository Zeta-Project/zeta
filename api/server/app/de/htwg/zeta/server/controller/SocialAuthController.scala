package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginEvent
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.ExtractableRequest
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfileBuilder
import com.mohiva.play.silhouette.impl.providers.SocialProvider
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import controllers.routes
import de.htwg.zeta.server.model.services.UserService
import de.htwg.zeta.server.util.auth.ZetaEnv
import grizzled.slf4j.Logging
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.Result

/**
 * The social auth controller.
 *
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info service implementation.
 * @param socialProviderRegistry The social provider registry.
 */
class SocialAuthController @Inject()(
    silhouette: Silhouette[ZetaEnv],
    userService: UserService,
    authInfoRepository: AuthInfoRepository,
    socialProviderRegistry: SocialProviderRegistry)
  extends Controller with Logging {

  /**
   * Authenticates a user against a social provider.
   *
   * @param provider The ID of the provider to authenticate against.
   * @return The result to display.
   */
  def authenticate(provider: String)(request: Request[AnyContent], messages: Messages): Future[Result] = {
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate()(ExtractableRequest.convertExplicit(request)).flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) => for {
            profile <- p.retrieveProfile(authInfo)
            user <- userService.save(profile)
            _ <- authInfoRepository.save(profile.loginInfo, authInfo)
            authenticator <- silhouette.env.authenticatorService.create(profile.loginInfo)(request)
            value <- silhouette.env.authenticatorService.init(authenticator)(request)
            result <- silhouette.env.authenticatorService.embed(value, Redirect(routes.ScalaRoutes.getIndex()))(request)
          } yield {
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            result
          }
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recover {
      case e: ProviderException =>
        error("Unexpected provider error", e)
        Redirect(routes.ScalaRoutes.getSignIn()).flashing("error" -> Messages("could.not.authenticate")(messages))
    }
  }
}
