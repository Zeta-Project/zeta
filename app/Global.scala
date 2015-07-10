import java.lang.reflect.Constructor

import models.{MongoDbUserService, SecureSocialUser}
import play.api.{Logger, Application}
import securesocial.core.providers.utils.PasswordHasher
import securesocial.core.services.SaveMode
import securesocial.core.{AuthenticationMethod, BasicProfile, RuntimeEnvironment}
import securesocial.core.providers.UsernamePasswordProvider

import scala.collection.immutable.ListMap

object Global extends play.api.GlobalSettings {

  val log = Logger(this getClass() getName())


  /**
   * Gets called from the play framework when the application started
   * creates two new [[SecureSocialUser]]s using the [[MongoDbUserService]] if there are no users
   */
  override def onStart(app:Application) ={
      if(MongoDbUserService.getNumberOfRegisteredUsers==0) {

        val testUser = new BasicProfile(
          providerId = "userpass",
          userId = "example@htwg-konstanz.de",
          firstName = Some("Example"),
          lastName = Some("Example"),
          fullName = Some("Testuser"),
          email = Some("example@htwg-konstanz.de"),
          avatarUrl = None,
          authMethod = AuthenticationMethod.UserPassword,
          oAuth1Info = None,
          oAuth2Info = None,
          passwordInfo = Some(new PasswordHasher.Default().hash("supersecretpassword"))
        )

        MongoDbUserService.save(profile = testUser, admin = false, mode = SaveMode.PasswordChange)
        // admin@htwg-konstanz.de:admin
        val admin = new BasicProfile(
          providerId = "userpass",
          userId = "admin@htwg-konstanz.de",
          firstName = Some("Admin"),
          lastName = Some("Admin"),
          fullName = Some("Adminuser"),
          email = Some("admin@htwg-konstanz.de"),
          avatarUrl = None,
          authMethod = AuthenticationMethod.UserPassword,
          oAuth1Info = None,
          oAuth2Info = None,
          passwordInfo = Some(new PasswordHasher.Default().hash("supersecretpassword"))
        )

        MongoDbUserService.save(profile = admin, admin = true, mode = SaveMode.PasswordChange)
      }
  }


  object UserServiceRuntimeEnv extends RuntimeEnvironment.Default[SecureSocialUser] {
    override lazy val userService = MongoDbUserService
    override lazy val providers = ListMap(
      include(new UsernamePasswordProvider(userService, avatarService, viewTemplates, passwordHashers))
    )
  }

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    // TODO: inject using subcut
    val instance = controllerClass.getConstructors.find { c =>
      val params = c.getParameterTypes
      params.length == 1 && params(0) == classOf[RuntimeEnvironment[SecureSocialUser]]
    }.map {
      _.asInstanceOf[Constructor[A]].newInstance(UserServiceRuntimeEnv)
    }
    instance.getOrElse(super.getControllerInstance(controllerClass))
  }
}