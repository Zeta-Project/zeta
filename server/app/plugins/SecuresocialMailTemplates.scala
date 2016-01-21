package plugins

import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import play.twirl.api.{Html, Txt}
import securesocial.controllers.MailTemplates
import securesocial.core.BasicProfile
import util.definitions.UserEnvironment

class SecuresocialMailTemplates(env: UserEnvironment) extends MailTemplates {

  implicit val implicitEnv = env

  override def getSignUpEmail(token: String)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.mails.SignUpEmail(token)))
  }

  override def getUnknownEmailNotice()(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.mails.UnknownEmailNotice()))
  }

  override def getSendPasswordResetEmail(user: BasicProfile, token: String)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.mails.PasswordResetEmail(user, token)))
  }

  override def getPasswordChangedNoticeEmail(user: BasicProfile)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.mails.PasswordChangeNotice(user)))
  }

  override def getWelcomeEmail(user: BasicProfile)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.mails.WelcomeEmail(user)))
  }

  override def getAlreadyRegisteredEmail(user: BasicProfile)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.mails.AlreadyRegisteredEmail(user)))
  }
}
