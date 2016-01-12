package controllers

import javax.inject.Inject

import models.SecureSocialUser
import play.api.data.Form
import play.api.data.Forms.{default, mapping, text, tuple}
import play.api.i18n.{Messages, I18nSupport, MessagesApi}
import util.definitions.UserEnvironment

case class AccountFormData(firstName: String = "", lastName: String = "", fullName: String = "", eMail: String = "", password: (String, String) = ("", ""))

class AccountController @Inject() (override implicit val env: UserEnvironment, val messagesApi: MessagesApi)
  extends securesocial.core.SecureSocial with I18nSupport {


  val accountDataForm = Form(
    mapping(
      "firstName" -> text,
      "lastName" -> text,
      "fullName" -> text,
      "eMail" -> text,
      "password" -> tuple(
        "main" -> default(text, ""),
        "confirm" -> default(text, "")
      ).verifying("Passwords don't match", password => password._1 == password._2)
    )(AccountFormData.apply)(AccountFormData.unapply)
  )

  def overview() = SecuredAction { implicit request =>
    Ok(views.html.webpage.AccountOverview.render(request.user))
  }

  def edit() = SecuredAction { implicit request =>
    val filledForm = accountDataForm.fill(accountData(request.user))
    Ok(views.html.webpage.AccountEdit.render(request.user, filledForm, implicitly[Messages]))
  }

  def saveChanges() = SecuredAction { implicit request =>
    accountDataForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.webpage.AccountEdit.render(request.user, formWithErrors, implicitly[Messages]))
      },
      userData => {
        Redirect(controllers.routes.AccountController.overview())
      })
  }

  def accountData(user: SecureSocialUser) = {
    AccountFormData(
      firstName = user.profile.firstName.getOrElse(""),
      lastName = user.profile.lastName.getOrElse(""),
      fullName = user.profile.fullName.getOrElse(""),
      eMail = user.profile.email.getOrElse("")
    )
  }

}
