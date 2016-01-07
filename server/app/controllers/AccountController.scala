package controllers

import models.SecureSocialUser
import securesocial.core.RuntimeEnvironment

class AccountController(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser] {

  def overview() = SecuredAction { implicit request =>
    Ok(views.html.webpage.AccountOverview.render(request.user))
  }

  def edit() = SecuredAction { implicit request =>
    Ok(views.html.webpage.AccountEdit.render(request.user))
  }

  def saveChanges() = SecuredAction { implicit request =>
    Ok("saved")
  }

}
