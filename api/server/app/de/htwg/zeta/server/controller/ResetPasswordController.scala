package de.htwg.zeta.server.controller

import java.util.UUID

import javax.inject.Inject

import scala.concurrent.Future
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import de.htwg.zeta.persistence.general.UserRepository
import de.htwg.zeta.server.controller.ResetPasswordController.error
import de.htwg.zeta.server.controller.ResetPasswordController.invalidResetLink
import de.htwg.zeta.server.forms.ResetPasswordForm
import de.htwg.zeta.server.model.TokenCache
import de.htwg.zeta.server.routing.routes
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.Result

/**
 * The `Reset Password` controller.
 *
 * @param authInfoRepository     The auth info repository.
 * @param passwordHasherRegistry The password hasher registry.
 */
class ResetPasswordController @Inject()(
    authInfoRepository: AuthInfoRepository,
    passwordHasherRegistry: PasswordHasherRegistry,
    tokenCache: TokenCache,
    userRepo: UserRepository
) extends Controller {

  /** Views the `Reset Password` page.
   *
   * @param token    The token to identify a user.
   * @param request  request
   * @param messages messages
   * @return The result to display.
   */
  def view(token: UUID)(request: Request[AnyContent], messages: Messages): Future[Result] = {
    tokenCache.read(token).map { _ =>
      Ok(views.html.silhouette.resetPassword(ResetPasswordForm.form, token, request, messages))
    }.recover {
      case _ => Redirect(routes.ScalaRoutes.getSignIn()).flashing(error -> messages(invalidResetLink))
    }
  }

  /** Resets the password.
   *
   * @param token    The token to identify a user.
   * @param request  request
   * @param messages messages
   * @return The result to display.
   */
  def submit(token: UUID)(request: Request[AnyContent], messages: Messages): Future[Result] = {
    tokenCache.read(token).flatMap[Result](userId => {
      ResetPasswordForm.form.bindFromRequest()(request).fold(
        form => Future(BadRequest(views.html.silhouette.resetPassword(form, token, request, messages))),
        password => {
          userRepo.read(userId).flatMap(user => {
            val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
            val passwordInfo = passwordHasherRegistry.current.hash(password)
            authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo).map { _ =>
              Redirect(routes.ScalaRoutes.getSignIn()).flashing("success" -> messages("password.reset"))
            }
          })
        })
    }).recover {
      case _ => Redirect(routes.ScalaRoutes.getSignIn()).flashing(error -> messages(invalidResetLink))
    }
  }

}

private object ResetPasswordController {

  private val invalidResetLink = "invalid.reset.link"
  private val error = "error"

}
