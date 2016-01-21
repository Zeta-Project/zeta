package plugins

import com.google.inject.Inject
import play.api.Play.current
import play.api.data.Form
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import securesocial.controllers.{ChangeInfo, RegistrationInfo, ViewTemplates}
import util.definitions.UserEnvironment

class SecuresocialViews @Inject() (env: UserEnvironment) extends ViewTemplates {

  val messagesApi = play.api.i18n.Messages.Implicits.applicationMessages.messages

  implicit val implicitEnv = env

  override def getLoginPage(form: Form[(String, String)], msg: Option[String])(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.Login.render(form, msg, request, messagesApi.preferred(request), env)

  override def getPasswordChangePage(form: Form[ChangeInfo])(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.PasswordChange.render(form, request, messagesApi.preferred(request), env)

  override def getNotAuthorizedPage(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.NotAuthorized.render(request, env)

  override def getStartSignUpPage(form: Form[String])(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.StartSignUp.render(form, request, messagesApi.preferred(request), env)

  override def getSignUpPage(form: Form[RegistrationInfo], token: String)(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.SignUp.render(form, token, request, messagesApi.preferred(request), env)

  override def getResetPasswordPage(form: Form[(String, String)], token: String)(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.ResetPassword.render(form, token, request, messagesApi.preferred(request), env)

  override def getStartResetPasswordPage(form: Form[String])(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.StartResetPassword.render(form, request, messagesApi.preferred(request), env)
}
