package de.htwg.zeta.server.controller

import java.net.URLDecoder
import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.routes
import de.htwg.zeta.server.model.services.AuthTokenService
import de.htwg.zeta.server.model.services.UserService
import play.api.i18n.Messages
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.mailer.Email
import play.api.libs.mailer.MailerClient
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.Result

/**
 * The `Activate Account` controller.
 *
 * @param userService      The user service implementation.
 * @param authTokenService The auth token service implementation.
 * @param mailerClient     The mailer client.
 */
class ActivateAccountController @Inject()(
    userService: UserService,
    authTokenService: AuthTokenService,
    mailerClient: MailerClient)
  extends Controller {

  /**
   * Sends an account activation email to the user with the given email.
   *
   * @param email The email address of the user to send the activation mail to.
   * @return The result to display.
   */
  def send(email: String)(request: Request[AnyContent], messages: Messages): Future[Result] = {
    val decodedEmail = URLDecoder.decode(email, "UTF-8")
    val loginInfo = LoginInfo(CredentialsProvider.ID, decodedEmail)
    val result = Redirect(routes.ScalaRoutes.getSignIn()).flashing("info" -> messages("activation.email.sent", decodedEmail))

    userService.retrieve(loginInfo).flatMap {
      case Some(user) if !user.activated =>
        authTokenService.create(user.id).map { authToken =>
          val url = routes.ScalaRoutes.getAccountActivate(authToken.id).absoluteURL()(request)

          mailerClient.send(Email(
            subject = messages("email.activate.account.subject"),
            from = messages("email.from"),
            to = Seq(decodedEmail),
            bodyText = Some(views.txt.silhouette.emails.activateAccount(user, url, messages).body),
            bodyHtml = Some(views.html.silhouette.emails.activateAccount(user, url, messages).body)
          ))
          result
        }
      case None => Future.successful(result)
    }
  }

  /**
   * Activates an account.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def activate(token: UUID)(request: Request[AnyContent], messages: Messages): Future[Result] = {
    authTokenService.validate(token).flatMap {
      case Some(authToken) => userService.retrieve(authToken.userID).flatMap {
        case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
          userService.save(user.copy(activated = true)).map { _ =>
            Redirect(routes.ScalaRoutes.getSignIn()).flashing("success" -> messages("account.activated"))
          }
        case _ => Future.successful(Redirect(routes.ScalaRoutes.getSignIn()).flashing("error" -> messages("invalid.activation.link")))
      }
      case None => Future.successful(Redirect(routes.ScalaRoutes.getSignIn()).flashing("error" -> messages("invalid.activation.link")))
    }
  }
}
