package de.htwg.zeta.server.controller

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.SignUpEvent
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.routes
import de.htwg.zeta.server.forms.SignUpForm
import de.htwg.zeta.server.forms.SignUpForm.Data
import de.htwg.zeta.server.model.services.AuthTokenService
import de.htwg.zeta.server.model.services.UserService
import de.htwg.zeta.server.util.auth.ZetaEnv
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
 * The `Sign Up` controller.
 *
 * @param silhouette             The Silhouette stack.
 * @param userService            The user service implementation.
 * @param authInfoRepository     The auth info repository implementation.
 * @param authTokenService       The auth token service implementation.
 * @param avatarService          The avatar service implementation.
 * @param passwordHasherRegistry The password hasher registry.
 * @param mailerClient           The mailer client.
 */
class SignUpController @Inject()(
    silhouette: Silhouette[ZetaEnv],
    userService: UserService,
    authInfoRepository: AuthInfoRepository,
    authTokenService: AuthTokenService,
    avatarService: AvatarService,
    passwordHasherRegistry: PasswordHasherRegistry,
    mailerClient: MailerClient)
  extends Controller {

  /**
   * Views the `Sign Up` page.
   *
   * @return The result to display.
   */
  def view(request: Request[AnyContent], messages: Messages): Future[Result] = {
    Future.successful(Ok(views.html.silhouette.signUp(SignUpForm.form, request, messages)))
  }

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit(request: Request[AnyContent], messages: Messages): Future[Result] = {
    SignUpForm.form.bindFromRequest()(request).fold(
      form => Future.successful(BadRequest(views.html.silhouette.signUp(form, request, messages))),
      data => {
        val result = Redirect(routes.ScalaRoutes.signUpView()).flashing("info" -> messages("sign.up.email.sent", data.email))
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) => processAlreadySignedUp(user, result, data, request, messages)
          case None => processSignUp(result, data, loginInfo, request, messages)
        }
      }
    )
  }

  private def processAlreadySignedUp(user: User, result: Result, data: SignUpForm.Data, request: Request[AnyContent], messages: Messages): Future[Result] = {
    val url = routes.ScalaRoutes.signInView().absoluteURL()(request)
    mailerClient.send(Email(
      subject = messages("email.already.signed.up.subject"),
      from = messages("email.from"),
      to = Seq(data.email),
      bodyText = Some(views.txt.silhouette.emails.alreadySignedUp(user, url, messages).body),
      bodyHtml = Some(views.html.silhouette.emails.alreadySignedUp(user, url, messages).body)
    ))

    Future.successful(result)
  }

  private def processSignUp(result: Result, data: Data, loginInfo: LoginInfo, request: Request[AnyContent], messages: Messages): Future[Result] = {
    val authInfo = passwordHasherRegistry.current.hash(data.password)
    val user = User(
      userID = UUID.randomUUID(),
      loginInfo = loginInfo,
      firstName = Some(data.firstName),
      lastName = Some(data.lastName),
      fullName = Some(data.firstName + " " + data.lastName),
      email = Some(data.email),
      avatarURL = None,
      activated = false
    )
    for {
      avatar <- avatarService.retrieveURL(data.email)
      user <- userService.save(user.copy(avatarURL = avatar))
      _ <- authInfoRepository.add(loginInfo, authInfo)
      authToken <- authTokenService.create(user.userID)
    } yield {
      val url = routes.ScalaRoutes.activateAccount(authToken.id).absoluteURL()(request)
      mailerClient.send(Email(
        subject = messages("email.sign.up.subject"),
        from = messages("email.from"),
        to = Seq(data.email),
        bodyText = Some(views.txt.silhouette.emails.signUp(user, url, messages).body),
        bodyHtml = Some(views.html.silhouette.emails.signUp(user, url, messages).body)
      ))

      silhouette.env.eventBus.publish(SignUpEvent(user, request))
      result
    }
  }
}
