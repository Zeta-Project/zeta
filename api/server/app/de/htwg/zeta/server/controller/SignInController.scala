package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

import com.mohiva.play.silhouette.api.Authenticator.Implicits.RichDateTime
import com.mohiva.play.silhouette.api.LoginEvent
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.routes
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.Persistence
import de.htwg.zeta.server.forms.SignInForm
import de.htwg.zeta.server.util.auth.ZetaEnv
import models.User
import net.ceedubs.ficus.Ficus.finiteDurationReader
import net.ceedubs.ficus.Ficus.optionValueReader
import net.ceedubs.ficus.Ficus.toFicusConfig
import play.api.Configuration
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.Result

/**
 * The `Sign In` controller.
 *
 * @param silhouette          The Silhouette stack.
 * @param credentialsProvider The credentials provider.
 * @param configuration       The Play configuration.
 * @param clock               The clock instance.
 */
class SignInController @Inject()(
    silhouette: Silhouette[ZetaEnv],
    credentialsProvider: CredentialsProvider,
    configuration: Configuration,
    clock: Clock)
  extends Controller {

  private val loginInfoPersistence: LoginInfoPersistence = Persistence.loginInfoPersistence
  private val userPersistence: Persistence[User] = Persistence.fullAccessRepository.users

  /** Views the `Sign In` page.
   *
   * @param request  The request
   * @param messages The messages
   * @return The result to display.
   */
  def view(request: Request[AnyContent], messages: Messages): Future[Result] = {
    Future.successful(Ok(views.html.silhouette.signIn(SignInForm.form, request, messages)))
  }

  /** Handles the submitted form.
   *
   * @param request  The request
   * @param messages The messages
   * @return The result to display.
   */
  def submit(request: Request[AnyContent], messages: Messages): Future[Result] = {
    SignInForm.form.bindFromRequest()(request).fold(
      form => Future.successful(BadRequest(views.html.silhouette.signIn(form, request, messages))),
      data => {
        credentialsProvider.authenticate(Credentials(data.email, data.password)).flatMap { loginInfo =>
          loginInfoPersistence.read(loginInfo).flatMap { userId =>
            userPersistence.read(userId).flatMap { user =>
              if (!user.activated) {
                Future.successful(Ok(views.html.silhouette.activateAccount(data.email, request, messages)))
              } else {
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
                    silhouette.env.authenticatorService.embed(v, Redirect("/"))(request)
                  }
                }
              }
            }
          }
        }.recover {
          case _: ProviderException =>
            Redirect(routes.ScalaRoutes.getSignIn()).flashing("error" -> messages("invalid.credentials"))
        }
      }
    )
  }
}
