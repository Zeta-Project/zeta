package de.htwg.zeta.server.controller

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.routes
import de.htwg.zeta.server.forms.ResetPasswordForm
import de.htwg.zeta.server.model.services.AuthTokenService
import de.htwg.zeta.server.model.services.UserService
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Request
import play.api.mvc.Result

/**
 * The `Reset Password` controller.
 *
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info repository.
 * @param passwordHasherRegistry The password hasher registry.
 * @param authTokenService       The auth token service implementation.
 */
class ResetPasswordController @Inject()(
    userService: UserService,
    authInfoRepository: AuthInfoRepository,
    passwordHasherRegistry: PasswordHasherRegistry,
    authTokenService: AuthTokenService)
  extends Controller {

  /**
   * Views the `Reset Password` page.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def view(token: UUID)(request: Request[AnyContent], messages: Messages): Future[Result] = {
    authTokenService.validate(token).map {
      case Some(authToken) => Ok(views.html.silhouette.resetPassword(ResetPasswordForm.form, token, request, messages))
      case None => Redirect(routes.ScalaRoutes.getSignIn()).flashing("error" -> messages("invalid.reset.link"))
    }
  }

  /**
   * Resets the password.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def submit(token: UUID)(request: Request[AnyContent], messages: Messages): Future[Result] = {
    authTokenService.validate(token).flatMap {
      case Some(authToken) =>
        ResetPasswordForm.form.bindFromRequest()(request).fold(
          form => Future.successful(BadRequest(views.html.silhouette.resetPassword(form, token, request, messages))),
          password => userService.retrieve(authToken.userID).flatMap {
            case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
              val passwordInfo = passwordHasherRegistry.current.hash(password)
              authInfoRepository.update[PasswordInfo](user.loginInfo, passwordInfo).map { _ =>
                Redirect(routes.ScalaRoutes.getSignIn()).flashing("success" -> messages("password.reset"))
              }
            case _ => Future.successful(Redirect(routes.ScalaRoutes.getSignIn()).flashing("error" -> messages("invalid.reset.link")))
          }
        )
      case None => Future.successful(Redirect(routes.ScalaRoutes.getSignIn()).flashing("error" -> messages("invalid.reset.link")))
    }
  }
}
