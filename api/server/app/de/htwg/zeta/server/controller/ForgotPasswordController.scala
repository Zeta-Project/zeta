package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.routes
import controllers.WebJarAssets
import de.htwg.zeta.server.forms.ForgotPasswordForm
import de.htwg.zeta.server.model.services.AuthTokenService
import de.htwg.zeta.server.model.services.UserService
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.mailer.Email
import play.api.libs.mailer.MailerClient
import play.api.mvc.Controller

/**
 * The `Forgot Password` controller.
 *
 * @param messagesApi      The Play messages API.
 * @param silhouette       The Silhouette stack.
 * @param userService      The user service implementation.
 * @param authTokenService The auth token service implementation.
 * @param mailerClient     The mailer client.
 * @param webJarAssets     The WebJar assets locator.
 */
class ForgotPasswordController @Inject() (
    val messagesApi: MessagesApi,
    silhouette: Silhouette[ZetaEnv],
    userService: UserService,
    authTokenService: AuthTokenService,
    mailerClient: MailerClient,
    implicit val webJarAssets: WebJarAssets)
  extends Controller with I18nSupport {

  /**
   * Views the `Forgot Password` page.
   *
   * @return The result to display.
   */
  def view = silhouette.UnsecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.silhouette.forgotPassword(ForgotPasswordForm.form)))
  }

  /**
   * Sends an email with password reset instructions.
   *
   * It sends an email to the given address if it exists in the database. Otherwise we do not show the user
   * a notice for not existing email addresses to prevent the leak of existing email addresses.
   *
   * @return The result to display.
   */
  def submit = silhouette.UnsecuredAction.async { implicit request =>
    ForgotPasswordForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.silhouette.forgotPassword(form))),
      email => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, email)
        val result = Redirect(routes.ScalaRoutes.signInView()).flashing("info" -> Messages("reset.email.sent"))
        userService.retrieve(loginInfo).flatMap {
          case Some(user) if user.email.isDefined =>
            authTokenService.create(user.userID).map { authToken =>
              val url = routes.ScalaRoutes.resetPasswordView(authToken.id).absoluteURL()

              mailerClient.send(Email(
                subject = Messages("email.reset.password.subject"),
                from = Messages("email.from"),
                to = Seq(email),
                bodyText = Some(views.txt.silhouette.emails.resetPassword(user, url).body),
                bodyHtml = Some(views.html.silhouette.emails.resetPassword(user, url).body)
              ))
              result
            }
          case None => Future.successful(result)
        }
      }
    )
  }
}
