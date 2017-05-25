package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

import com.mohiva.play.silhouette.api.LoginEvent
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.Authenticator.Implicits.RichDateTime
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import controllers.routes
import de.htwg.zeta.server.forms.SignInForm
import de.htwg.zeta.server.model.services.UserService
import de.htwg.zeta.server.util.auth.ZetaEnv
import net.ceedubs.ficus.Ficus.finiteDurationReader
import net.ceedubs.ficus.Ficus.optionValueReader
import net.ceedubs.ficus.Ficus.toFicusConfig
import play.api.Configuration
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Request
import play.api.mvc.Result

/**
 * The `Sign In` controller.
 *
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param credentialsProvider    The credentials provider.
 * @param socialProviderRegistry The social provider registry.
 * @param configuration          The Play configuration.
 * @param clock                  The clock instance.
 */
class SignInController @Inject()(
    silhouette: Silhouette[ZetaEnv],
    userService: UserService,
    credentialsProvider: CredentialsProvider,
    socialProviderRegistry: SocialProviderRegistry,
    configuration: Configuration,
    clock: Clock)
  extends Controller {

  /**
   * Views the `Sign In` page.
   *
   * @return The result to display.
   */
  def view(request: Request[AnyContent], messages: Messages): Future[Result] = {
    Future.successful(Ok(views.html.silhouette.signIn(SignInForm.form, socialProviderRegistry, request, messages)))
  }

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit(request: Request[AnyContent], messages: Messages): Future[Result] = {
    SignInForm.form.bindFromRequest()(request).fold(
      form => Future.successful(BadRequest(views.html.silhouette.signIn(form, socialProviderRegistry, request, messages))),
      data => {
        val credentials = Credentials(data.email, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          val result = Redirect("/")
          userService.retrieve(loginInfo).flatMap {
            case Some(user) if !user.activated =>
              Future.successful(Ok(views.html.silhouette.activateAccount(data.email, request, messages)))
            case Some(user) =>
              val c = configuration.underlying
              silhouette.env.authenticatorService.create(loginInfo)(request).map { authenticator =>
                if (data.rememberMe) {
                  authenticator.copy(
                    expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
                    idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout"),
                    cookieMaxAge = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.cookieMaxAge")
                  )
                } else {
                  authenticator
                }
              }.flatMap { authenticator =>
                silhouette.env.eventBus.publish(LoginEvent(user, request))
                silhouette.env.authenticatorService.init(authenticator)(request).flatMap { v =>
                  silhouette.env.authenticatorService.embed(v, result)(request)
                }
              }
            case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }.recover {
          case _: ProviderException =>
            Redirect(routes.ScalaRoutes.getSignIn()).flashing("error" -> messages("invalid.credentials"))
        }
      }
    )
  }
}
