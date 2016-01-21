package plugins

import com.google.inject.Inject
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.data.Form
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import securesocial.controllers.{ChangeInfo, RegistrationInfo, ViewTemplates}
import util.definitions.UserEnvironment

class SecuresocialViews @Inject()(env: UserEnvironment) extends ViewTemplates {

  implicit val implicitEnv = env

  override def getLoginPage(form: Form[(String, String)], msg: Option[String])(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.Login(form, msg)

  override def getPasswordChangePage(form: Form[ChangeInfo])(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.PasswordChange(form)

  override def getNotAuthorizedPage(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.NotAuthorized()

  override def getStartSignUpPage(form: Form[String])(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.StartSignUp(form)

  override def getSignUpPage(form: Form[RegistrationInfo], token: String)(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.SignUp(form, token)

  override def getResetPasswordPage(form: Form[(String, String)], token: String)(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.ResetPassword(form, token)

  override def getStartResetPasswordPage(form: Form[String])(implicit request: RequestHeader, lang: Lang): Html = views.html.auth.StartResetPassword(form)
}
