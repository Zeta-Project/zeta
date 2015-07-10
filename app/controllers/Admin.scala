package controllers

import models._
import securesocial.core.RuntimeEnvironment

class Admin(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser]{

  def index() = SecuredAction { implicit request =>
    Ok(views.html.index.render(Some(request.user)))
  }
}