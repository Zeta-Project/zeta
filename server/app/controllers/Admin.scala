package controllers

import akka.actor.ActorRef
import models._
import securesocial.core.RuntimeEnvironment

class Admin(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser]{
}