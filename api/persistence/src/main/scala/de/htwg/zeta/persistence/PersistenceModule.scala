package de.htwg.zeta.persistence

import java.util.concurrent.TimeUnit
import javax.inject.Singleton

import scala.concurrent.Future
import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.typesafe.config.ConfigFactory
import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.EventDrivenTask
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.Log
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.entity.TimedTask
import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedFilePersistence
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedLogRepository
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedMetaModelEntityRepository
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedModelEntityRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheEntityRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheFilePersistence
import de.htwg.zeta.persistence.actorCache.ActorCacheLoginInfoRepository
import de.htwg.zeta.persistence.actorCache.ActorCachePasswordInfoRepository
import de.htwg.zeta.persistence.general.EntityRepository
import de.htwg.zeta.persistence.general.FileRepository
import de.htwg.zeta.persistence.general.LoginInfoRepository
import de.htwg.zeta.persistence.general.PasswordInfoRepository
import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.persistence.mongo.MongoEntityRepository
import de.htwg.zeta.persistence.mongo.MongoFilePersistence
import de.htwg.zeta.persistence.mongo.MongoHandler
import de.htwg.zeta.persistence.mongo.MongoLoginInfoRepository
import de.htwg.zeta.persistence.mongo.MongoPasswordInfoRepository
import de.htwg.zeta.persistence.transient.TransientTokenCache
import grizzled.slf4j.Logging
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.api.DefaultDB
import reactivemongo.api.MongoDriver

/**
 * The Guice module which wires all Persistence dependencies.
 */
class PersistenceModule extends AbstractModule with ScalaModule with Logging {

  /**
   * Configures the module.
   */
  def configure(): Unit = {
    bind[TokenCache].to[TransientTokenCache]
    bind[AccessRestrictedFilePersistence]
    bind[AccessRestrictedMetaModelEntityRepository]
    bind[AccessRestrictedModelEntityRepository]
    bind[AccessRestrictedLogRepository]
  }

  private val settingServer = "zeta.mongodb.server"
  private val settingPort = "zeta.mongodb.port"
  private val settingDb = "zeta.mongodb.db"
  private val settingsUsername = "zeta.mongodb.username"
  private val settingsPassword = "zeta.mongodb.password"
  private val defaultServer = "localhost"
  private val defaultPort = "27017"
  private val defaultDb = "zeta"
  private val defaultUsername = ""
  private val defaultPassword = ""
  private val config = ConfigFactory.load()

  @Provides @Singleton
  def provideConnection(): Future[DefaultDB] = {

    def getString(path: String, default: String): String = {
      if (config.hasPath(path)) config.getString(path) else default
    }

    def getInt(path: String, default: String): String = {
      if (config.hasPath(path)) config.getInt(path).toString else default
    }

    val configServer = getString(settingServer, defaultServer)
    val configPort = getInt(settingPort, defaultPort)
    val configDb = getString(settingDb, defaultDb)
    val usernameAndPassword: String = {
      val username = getString(settingsUsername, defaultUsername)
      val password = getString(settingsPassword, defaultPassword)
      if (username.isEmpty || password.isEmpty) "" else s"$username:$password@"
    }
    info(s"Mongo connection: $usernameAndPassword$configServer:$configPort") // scalastyle:ignore multiple.string.literals
    val uri = s"$usernameAndPassword$configServer:$configPort/$configDb"
    Future.fromTry(MongoDriver().connection(uri)).flatMap(_.database(configDb))
  }

  private val system = ActorSystem("actorCache")
  private val numberActorsPerType = 10 // scalastyle:ignore magic.number
  private val cacheDuration = Duration(10, TimeUnit.MINUTES) // scalastyle:ignore magic.number
  private val timeout = Duration(10, TimeUnit.SECONDS) // scalastyle:ignore magic.number

  @Provides @Singleton
  def provideUserEntityRepo(connection: Future[DefaultDB]): EntityRepository[User] = {
    new ActorCacheEntityRepository[User](
      new MongoEntityRepository[User](connection, MongoHandler.userHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideMetaModelEntityRepo(connection: Future[DefaultDB]): EntityRepository[MetaModelEntity] = {
    new ActorCacheEntityRepository(
      new MongoEntityRepository(connection, MongoHandler.metaModelEntityHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideModelEntityRepo(connection: Future[DefaultDB]): EntityRepository[ModelEntity] = {
    new ActorCacheEntityRepository(
      new MongoEntityRepository(connection, MongoHandler.modelEntityHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideFileRepo(connection: Future[DefaultDB]): FileRepository = {
    new ActorCacheFilePersistence(
      new MongoFilePersistence(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideGeneratorRepo(connection: Future[DefaultDB]): EntityRepository[Generator] = {
    new ActorCacheEntityRepository(
      new MongoEntityRepository(connection, MongoHandler.generatorHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideFilterRepo(connection: Future[DefaultDB]): EntityRepository[Filter] = {
    new ActorCacheEntityRepository(
      new MongoEntityRepository(connection, MongoHandler.filterHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideGeneratorImageRepo(connection: Future[DefaultDB]): EntityRepository[GeneratorImage] = {
    new ActorCacheEntityRepository(
      new MongoEntityRepository(connection, MongoHandler.generatorImageHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideMetaModelReleaseRepo(connection: Future[DefaultDB]): EntityRepository[MetaModelRelease] = {
    new ActorCacheEntityRepository(
      new MongoEntityRepository(connection, MongoHandler.metaModelReleaseHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideLoginInfoRepo(connection: Future[DefaultDB]): LoginInfoRepository = {
    new ActorCacheLoginInfoRepository(
      new MongoLoginInfoRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def providePasswordInfoRepo(connection: Future[DefaultDB]): PasswordInfoRepository = {
    new ActorCachePasswordInfoRepository(
      new MongoPasswordInfoRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideAccessAuthorisationRepo(connection: Future[DefaultDB]): EntityRepository[AccessAuthorisation] = {
    new ActorCacheEntityRepository(
      new MongoEntityRepository(connection, MongoHandler.accessAuthorisationHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideLogRepo(connection: Future[DefaultDB]): EntityRepository[Log] = {
    new ActorCacheEntityRepository(
      new MongoEntityRepository(connection, MongoHandler.logHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideBondedTaskRepo(connection: Future[DefaultDB]): EntityRepository[BondedTask] = {
    new ActorCacheEntityRepository(
      new MongoEntityRepository(connection, MongoHandler.bondedTaskHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideEventDrivenTaskRepo(connection: Future[DefaultDB]): EntityRepository[EventDrivenTask] = {
    new ActorCacheEntityRepository(
      new MongoEntityRepository(connection, MongoHandler.eventDrivenTaskHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideTimedTaskRepo(connection: Future[DefaultDB]): EntityRepository[TimedTask] = {
    new ActorCacheEntityRepository(
      new MongoEntityRepository(connection, MongoHandler.timedTaskHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

}
