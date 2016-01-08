package util.definitions

/**
  * Created by Felix on 07.01.2016.
  */
import com.google.inject.{ Inject, Singleton }
import models.{MongoDbUserService, SecureSocialUser}
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.{ BasicProfile, RuntimeEnvironment }

import scala.collection.immutable.ListMap

class UserEnvironment extends RuntimeEnvironment.Default {
  override type U = SecureSocialUser
  override implicit val executionContext = play.api.libs.concurrent.Execution.defaultContext
  //override lazy val routes = new CustomRoutesService()
  override lazy val userService = new MongoDbUserService()
  override lazy val providers = ListMap(
    include(new UsernamePasswordProvider(userService, avatarService, viewTemplates, passwordHashers))
  )
  //override lazy val eventListeners = List(new MyEventListener())
}
