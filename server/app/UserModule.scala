import com.google.inject.{ TypeLiteral, AbstractModule }
import dao.metaModel.{ZetaMetaModelDao, MetaModelDao}
import dao.model.{ZetaModelDao, ModelDao}
import net.codingwell.scalaguice.ScalaModule
import securesocial.core.RuntimeEnvironment
import util.definitions.UserEnvironment

class UserModule extends AbstractModule with ScalaModule {
  override def configure() {
    // SecureSocial
    val environment = new UserEnvironment()
    bind(new TypeLiteral[RuntimeEnvironment] {}).toInstance(environment)
    // DAO-Classes
    bind[ZetaMetaModelDao].to[MetaModelDao].asEagerSingleton()
    bind[ZetaModelDao].to[ModelDao].asEagerSingleton()
  }
}

