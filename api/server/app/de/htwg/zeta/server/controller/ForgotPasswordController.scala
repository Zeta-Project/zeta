package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.routes
import de.htwg.zeta.server.forms.ForgotPasswordForm
import de.htwg.zeta.server.model.services.AuthTokenService
import de.htwg.zeta.server.model.services.UserService
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.mailer.Email
import play.api.libs.mailer.MailerClient
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Result
import play.api.mvc.Request

/**
 * The `Forgot Password` controller.
 *
 * @param userService      The user service implementation.
 * @param authTokenService The auth token service implementation.
 * @param mailerClient     The mailer client.
 */
class ForgotPasswordController @Inject()(
    userService: UserService,
    authTokenService: AuthTokenService,
    mailerClient: MailerClient)
  extends Controller {

  /**
   * Views the `Forgot Password` page.
   *
   * @return The result to display.
   */
  def view(request: Request[AnyContent], messages: Messages): Future[Result] = {
    Future.successful(Ok(views.html.silhouette.forgotPassword(ForgotPasswordForm.form, request, messages)))
  }

  /**
   * Sends an email with password reset instructions.
   *
   * It sends an email to the given address if it exists in the database. Otherwise we do not show the user
   * a notice for not existing email addresses to prevent the leak of existing email addresses.
   *
   * @return The result to display.
   */
  def submit(request: Request[AnyContent], messages: Messages): Future[Result] = {
    ForgotPasswordForm.form.bindFromRequest()(request).fold(
      form => Future.successful(BadRequest(views.html.silhouette.forgotPassword(form, request, messages))),
      email => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, email)
        val result = Redirect(routes.ScalaRoutes.getSignIn()).flashing("info" -> messages("reset.email.sent"))
        userService.retrieve(loginInfo).flatMap {
          case Some(user) if user.email.isDefined =>
            authTokenService.create(user.userID).map { authToken =>
              val url = routes.ScalaRoutes.getPasswordReset(authToken.id).absoluteURL()(request)

              mailerClient.send(Email(
                subject = messages("email.reset.password.subject"),
                from = messages("email.from"),
                to = Seq(email),
                bodyText = Some(views.txt.silhouette.emails.resetPassword(user, url, messages).body),
                bodyHtml = Some(views.html.silhouette.emails.resetPassword(user, url, messages).body)
              ))
              result
            }
          case None => Future.successful(result)
        }
      }
    )
  }
}
