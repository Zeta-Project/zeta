package de.htwg.zeta.server.controller

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.routes
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.Persistence
import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.server.forms.ForgotPasswordForm
import models.User
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.mailer.Email
import play.api.libs.mailer.MailerClient
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.Result

/**
 * The `Forgot Password` controller.
 *
 * @param mailerClient The mailer client.
 */
class ForgotPasswordController @Inject()(
    mailerClient: MailerClient)
  extends Controller {

  private val tokenCache: TokenCache = Persistence.tokenCache
  private val loginInfoPersistence: LoginInfoPersistence = Persistence.loginInfoPersistence
  private val userPersistence: Persistence[UUID, User] = Persistence.service.user

  /** Views the `Forgot Password` page.
   *
   * @param request  The request
   * @param messages The messages
   * @return The result to display.
   */
  def view(request: Request[AnyContent], messages: Messages): Future[Result] = {
    Future.successful(Ok(views.html.silhouette.forgotPassword(ForgotPasswordForm.form, request, messages)))
  }


  /** Sends an email with password reset instructions.
   *
   * It sends an email to the given address if it exists in the database. Otherwise we do not show the user
   * a notice for not existing email addresses to prevent the leak of existing email addresses.
   *
   * @param request  The request
   * @param messages The messages
   * @return The result to display.
   */
  def submit(request: Request[AnyContent], messages: Messages): Future[Result] = {
    ForgotPasswordForm.form.bindFromRequest()(request).fold(
      form => Future.successful(BadRequest(views.html.silhouette.forgotPassword(form, request, messages))),
      email => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, email)
        val result = Redirect(routes.ScalaRoutes.getSignIn()).flashing("info" -> messages("reset.email.sent"))
        val userId = loginInfoPersistence.read(loginInfo)
        val user = userId.flatMap(userId => userPersistence.read(userId))
        user.flatMap(user => {
          tokenCache.create(user.id).map { token =>
            val url = routes.ScalaRoutes.getPasswordReset(token).absoluteURL()(request)
            mailerClient.send(Email(
              subject = messages("email.reset.password.subject"),
              from = messages("email.from"),
              to = Seq(email),
              bodyText = Some(views.txt.silhouette.emails.resetPassword(user, url, messages).body),
              bodyHtml = Some(views.html.silhouette.emails.resetPassword(user, url, messages).body)
            ))
            result
          }
        }).recover {
          case _ => result
        }
      }
    )
  }

}
