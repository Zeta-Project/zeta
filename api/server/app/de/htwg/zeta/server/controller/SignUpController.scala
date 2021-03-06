package de.htwg.zeta.server.controller

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.SignUpEvent
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.persistence.general.UserRepository
import de.htwg.zeta.server.forms.SignUpForm
import de.htwg.zeta.server.forms.SignUpForm.Data
import de.htwg.zeta.server.model.TokenCache
import de.htwg.zeta.server.routing.routes
import de.htwg.zeta.server.silhouette.SilhouetteLoginInfoDao
import de.htwg.zeta.server.silhouette.ZetaEnv
import de.htwg.zeta.server.silhouette.ZetaIdentity
import play.api.i18n.Messages
import play.api.libs.mailer.Email
import play.api.libs.mailer.MailerClient
import play.api.mvc.AnyContent
import play.api.mvc.InjectedController
import play.api.mvc.Request
import play.api.mvc.Result

/**
 * The `Sign Up` controller.
 *
 * @param silhouette             The Silhouette stack.
 * @param authInfoRepository     The auth info repository implementation.
 * @param passwordHasherRegistry The password hasher registry.
 * @param mailerClient           The mailer client.
 */
class SignUpController @Inject()(
    silhouette: Silhouette[ZetaEnv],
    authInfoRepository: AuthInfoRepository,
    passwordHasherRegistry: PasswordHasherRegistry,
    mailerClient: MailerClient,
    userRepo: UserRepository,
    loginInfoRepo: SilhouetteLoginInfoDao,
    tokenCache: TokenCache,
  implicit val ec: ExecutionContext
) extends InjectedController {

  def submit_json(request: Request[AnyContent], messages: Messages): Future[Result] = {
    SignUpForm.form.bindFromRequest()(request).fold(
      form => Future.successful(NotAcceptable),
      data => {
        val result = Ok
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        val userId = loginInfoRepo.read(loginInfo)
        userId.flatMap(userId =>
          userRepo.read(userId).flatMap(user =>
            processAlreadySignedUp(user, BadRequest, data, request, messages)
          )).recoverWith {
          case _ => processSignUp(result, data, loginInfo, request, messages)
        }
      }
    )
  }

  private def processAlreadySignedUp(user: User, result: Result, data: SignUpForm.Data, request: Request[AnyContent], messages: Messages): Future[Result] = {
    mailerClient.send(Email(
      subject = messages("email.already.signed.up.subject"),
      from = messages("email.from"),
      to = Seq(data.email),
      bodyText = Some("Already signed up")
    ))

    Future.successful(result)
  }

  private def processSignUp(result: Result, data: Data, loginInfo: LoginInfo, request: Request[AnyContent], messages: Messages): Future[Result] = {
    val authInfo = passwordHasherRegistry.current.hash(data.password)
    val user = User(id = UUID.randomUUID(), firstName = data.firstName, lastName = data.lastName, email = data.email, activated = false)

    for {
      _ <- userRepo.create(user)
      _ <- loginInfoRepo.create(loginInfo, user.id)
      _ <- authInfoRepository.add(loginInfo, authInfo)
      token <- tokenCache.create(user.id)
    } yield {
      val url = routes.ScalaRoutes.getAccountActivate(token).absoluteURL()(request)
      mailerClient.send(Email(
        subject = messages("email.sign.up.subject"),
        from = messages("email.from"),
        to = Seq(data.email),
        bodyText = Some(views.txt.silhouette.emails.signUp(user, url, messages).body),
        bodyHtml = Some(views.html.silhouette.emails.signUp(user, url, messages).body)
      ))

      silhouette.env.eventBus.publish(SignUpEvent(ZetaIdentity(user), request))
      result
    }
  }
}
