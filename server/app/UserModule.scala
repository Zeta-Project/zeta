import com.google.inject.{ TypeLiteral, Scopes, AbstractModule }
import models.oAuth.OAuthDataHandler
import models.{SecureSocialUser, MongoDbUserService}
import net.codingwell.scalaguice.ScalaModule
import securesocial.core.services.UserService
import securesocial.core.{ BasicProfile, RuntimeEnvironment }
import util.definitions.UserEnvironment

import scalaoauth2.provider.DataHandler

class UserModule extends AbstractModule with ScalaModule {
  override def configure() {

    val environment = new UserEnvironment()
    bind(new TypeLiteral[RuntimeEnvironment] {}).toInstance(environment)

  }
}

