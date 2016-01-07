package controllers

import models.SecureSocialUser
import play.api.data.Form
import play.api.data.Forms.{mapping, text, tuple}
import play.api.mvc.AnyContent
import securesocial.core.RuntimeEnvironment

case class AccountFormData(firstName: String = "", lastName: String = "", fullName: String = "", eMail: String = "", password: (String, String) = ("", ""))

class AccountController(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser] {


  val accountDataForm = Form(
    mapping(
      "firstName" -> text,
      "lastName" -> text,
      "fullName" -> text,
      "eMail" -> text,
      "password" -> tuple(
        "main" -> text,
        "confirm" -> text
      ).verifying("Passwords don't match", password => password._1 == password._2)
    )(AccountFormData.apply)(AccountFormData.unapply)
  )

  def overview() = SecuredAction { implicit request =>
    Ok(views.html.webpage.AccountOverview.render(request.user))
  }

  def edit() = SecuredAction { request =>
    val filledForm = accountDataForm.fill(accountData(request.user))
    Ok(views.html.webpage.AccountEdit.render(request.user, filledForm))
  }

  def saveChanges() = SecuredAction { implicit request =>
    accountDataForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.webpage.AccountEdit.render(request.user, formWithErrors))
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
