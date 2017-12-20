package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.routes
import de.htwg.zeta.server.forms.ChangePasswordForm
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result


/**
 * The `Change Password` controller.
 *
 * @param credentialsProvider    The credentials provider.
 * @param authInfoRepository     The auth info repository.
 * @param passwordHasherRegistry The password hasher registry.
 */
class ChangePasswordController @Inject()(
    credentialsProvider: CredentialsProvider,
    authInfoRepository: AuthInfoRepository,
    passwordHasherRegistry: PasswordHasherRegistry)
  extends Controller {

  /**
   * Views the `Change Password` page.
   *
   * @param request  The request
   * @param messages The messages
   * @return The result to display.
   */
  def view(request: SecuredRequest[ZetaEnv, AnyContent], messages: Messages): Result = {
    Ok(views.html.silhouette.changePassword(ChangePasswordForm.form, request.identity.user, request, messages))
  }

  /**
   * Changes the password.
   *
   * @param request  The request
   * @param messages The messages
   * @return The result to display.
   */
  def submit(request: SecuredRequest[ZetaEnv, AnyContent], messages: Messages): Future[Result] = {
    ChangePasswordForm.form.bindFromRequest()(request).fold(
      form => Future.successful(BadRequest(views.html.silhouette.changePassword(form, request.identity.user, request, messages))),
      password => {
        val (currentPassword, newPassword) = password // scalastyle:ignore
        val credentials = Credentials(request.identity.email, currentPassword)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          val passwordInfo = passwordHasherRegistry.current.hash(newPassword)
          authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo).map { _ =>
            Redirect(routes.ScalaRoutes.getPasswordChange()).flashing("success" -> messages("password.changed"))
          }
        }.recover {
          case _: ProviderException =>
            Redirect(routes.ScalaRoutes.getPasswordChange()).flashing("error" -> messages("current.password.invalid"))
        }
      }
    )
  }

}
