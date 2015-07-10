package controllers

import models._
import play.api.Logger
import securesocial.core.RuntimeEnvironment

class App(override implicit val env: RuntimeEnvironment[SecureSocialUser])

  extends securesocial.core.SecureSocial[SecureSocialUser] {

  val log = Logger(this getClass() getName())
}
