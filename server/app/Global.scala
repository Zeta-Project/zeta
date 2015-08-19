import java.lang.reflect.Constructor

import akka.actor.{Props, ActorRef}
import models.{CodeDocManagingActor, SecureSocialUser, MongoDbUserService}
import securesocial.core.RuntimeEnvironment
import securesocial.core.providers.UsernamePasswordProvider

import scala.collection.immutable.ListMap

object Global extends play.api.GlobalSettings {

  object UserServiceRuntimeEnv extends RuntimeEnvironment.Default[SecureSocialUser] {
    override lazy val userService = MongoDbUserService
    override lazy val providers = ListMap(
      include(new UsernamePasswordProvider(userService, avatarService, viewTemplates, passwordHashers))
    )
  }

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    val instance = controllerClass.getConstructors.find { c =>
      val params = c.getParameterTypes
      params.length == 1 && params(0) == classOf[RuntimeEnvironment[SecureSocialUser]]
    }.map {
      _.asInstanceOf[Constructor[A]].newInstance(UserServiceRuntimeEnv)
    }

    instance.getOrElse(super.getControllerInstance(controllerClass))
  }

}