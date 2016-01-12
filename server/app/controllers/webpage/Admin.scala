package controllers.webpage

import javax.inject.Inject

import models._
import securesocial.core.RuntimeEnvironment
import util.definitions.UserEnvironment

class Admin @Inject() (override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {
}