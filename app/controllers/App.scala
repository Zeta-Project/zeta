package controllers

import models._
import play.api.Logger
import securesocial.core.RuntimeEnvironment

class App(override implicit val env: RuntimeEnvironment[SecureSocialUser])

  extends securesocial.core.SecureSocial[SecureSocialUser] {

  val log = Logger(this getClass() getName())

  def index() = SecuredAction { implicit request =>
    Ok(views.html.index.render(Some(request.user)))
  }

  def metaModelEditor() = SecuredAction { implicit request =>
    Ok(views.html.metaModelEditor.render())
  }
}
