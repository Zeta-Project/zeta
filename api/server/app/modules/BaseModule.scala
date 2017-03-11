package modules

import com.google.inject.{ AbstractModule, Provides }
import models.daos.{ AuthTokenDAO, AuthTokenDAOImpl }
import models.services.{ AuthTokenService, AuthTokenServiceImpl }
import models.session.{ Session, SyncGatewaySession }
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.ws.WSClient
import utils.auth.{ HttpRepositoryFactory, RepositoryFactory }

/**
 * The base Guice module.
 */
class BaseModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  def configure(): Unit = {
    bind[AuthTokenDAO].to[AuthTokenDAOImpl]
    bind[AuthTokenService].to[AuthTokenServiceImpl]
  }

  /**
   * Provides the Session handler implementation.
   *
   * @param client Play's WS client.
   * @return The Session handler implementation.
   */
  @Provides
  def provideHTTPLayer(implicit client: WSClient): Session = new SyncGatewaySession()

  /**
   * Provide the Repository factor to access the database
   *
   * @param client Play's WS client.
   * @return The Repository factory implementation
   */
  @Provides
  def provideRepositoryFactory(implicit client: WSClient): RepositoryFactory = new HttpRepositoryFactory()
}
