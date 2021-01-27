package de.htwg.zeta.server.controller

import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import de.htwg.zeta.persistence.general.UserRepository
import de.htwg.zeta.server.forms.ForgotPasswordForm
import de.htwg.zeta.server.model.TokenCache
import de.htwg.zeta.server.routing.routes
import de.htwg.zeta.server.silhouette.SilhouetteLoginInfoDao
import play.api.i18n.Messages
import play.api.libs.mailer.Email
import play.api.libs.mailer.MailerClient
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import play.api.mvc.Result

/**
 * The `Forgot Password` controller.
 *
 * @param mailerClient The mailer client.
 */
class ForgotPasswordController @Inject()(
    val controllerComponents: ControllerComponents,
    mailerClient: MailerClient,
    loginInfoRepo: SilhouetteLoginInfoDao,
    userRepo: UserRepository,
    tokenCache: TokenCache,
    implicit val ec: ExecutionContext
) extends BaseController {

  // TODO: New Workflow. See: https://github.com/Zeta-Project/zeta/issues/456
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
      form => Future.successful(NotAcceptable),
      email => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, email)
        val result = Ok
        val userId = loginInfoRepo.read(loginInfo)
        val user = userId.flatMap(userId => userRepo.read(userId))
        user.flatMap(user => {
          tokenCache.create(user.id).map { token =>
            // TODO: Replace URL
            val url = routes.ScalaRoutes.postPasswordForgot().absoluteURL()(request)
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
          case _ => NotAcceptable
        }
      }
    )
  }

}
