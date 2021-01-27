package de.htwg.zeta.server.controller

import java.net.URLDecoder
import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import de.htwg.zeta.persistence.general.UserRepository
import de.htwg.zeta.server.model.TokenCache
import de.htwg.zeta.server.routing.routes
import de.htwg.zeta.server.silhouette.SilhouetteLoginInfoDao
import play.api.i18n.Messages
import play.api.libs.mailer.Email
import play.api.libs.mailer.MailerClient
import play.api.mvc.AnyContent
import play.api.mvc.InjectedController
import play.api.mvc.Request
import play.api.mvc.Result

/**
 * The `Activate Account` controller.
 *
 * @param mailerClient The mailer client.
 */
class ActivateAccountController @Inject()(
    mailerClient: MailerClient,
    tokenCache: TokenCache,
    userRepo: UserRepository,
    loginInfoRepo: SilhouetteLoginInfoDao,
  implicit val ec: ExecutionContext
) extends InjectedController {

  /** Sends an account activation email to the user with the given email.
   *
   * @param email    The email address of the user to send the activation mail to.
   * @param request  request
   * @param messages messages
   * @return The result to display.
   */
  def send(email: String)(request: Request[AnyContent], messages: Messages): Future[Result] = {
    val decodedEmail = URLDecoder.decode(email, "UTF-8")
    val loginInfo = LoginInfo(CredentialsProvider.ID, decodedEmail)

    val userId = loginInfoRepo.read(loginInfo)
    val user = userId.flatMap(userId => userRepo.read(userId))

    user.map { user =>
      if (!user.activated) {
        tokenCache.create(user.id).map { id =>
          val url = routes.ScalaRoutes.getAccountActivate(id).absoluteURL()(request)
          mailerClient.send(Email(
            subject = messages("email.activate.account.subject"),
            from = messages("email.from"),
            to = Seq(decodedEmail),
            bodyText = Some(views.txt.silhouette.emails.activateAccount(user, url, messages).body),
            bodyHtml = Some(views.html.silhouette.emails.activateAccount(user, url, messages).body)
          ))
        }
        Accepted
      } else {
        Ok
      }
    }.recover {
      case _ => Forbidden
    }

  }

  /** Activates an account.
   *
   * @param token    token The token to identify a user.
   * @param request  request
   * @param messages messages
   * @return The result to display.
   */
  def activate(token: UUID)(request: Request[AnyContent], messages: Messages): Future[Result] = {
    tokenCache.read(token).flatMap(userId =>
      userRepo.update(userId, _.copy(activated = true)).map(_ => Accepted)
    ).recover {
      case _ => Forbidden("Invalid activation link")
    }
  }

}
