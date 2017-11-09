package de.htwg.zeta.persistence

import java.util.concurrent.TimeUnit
import javax.inject.Singleton

import scala.concurrent.Future
import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.typesafe.config.ConfigFactory
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedFilePersistence
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedLogRepository
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedMetaModelEntityRepository
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedModelEntityRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheAccessAuthorisationRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheBondedTaskRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheEventDrivenTaskRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheFileRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheFilterImageRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheFilterRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheGeneratorImageRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheGeneratorRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheLoginInfoRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheLogRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheMetaModelEntityRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheMetaModelReleaseRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheModelEntityRepository
import de.htwg.zeta.persistence.actorCache.ActorCachePasswordInfoRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheSettingsRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheTimedTaskRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheUserRepository
import de.htwg.zeta.persistence.general.AccessAuthorisationRepository
import de.htwg.zeta.persistence.general.BondedTaskRepository
import de.htwg.zeta.persistence.general.EventDrivenTaskRepository
import de.htwg.zeta.persistence.general.FileRepository
import de.htwg.zeta.persistence.general.FilterImageRepository
import de.htwg.zeta.persistence.general.FilterRepository
import de.htwg.zeta.persistence.general.GeneratorImageRepository
import de.htwg.zeta.persistence.general.GeneratorRepository
import de.htwg.zeta.persistence.general.LoginInfoRepository
import de.htwg.zeta.persistence.general.LogRepository
import de.htwg.zeta.persistence.general.MetaModelEntityRepository
import de.htwg.zeta.persistence.general.MetaModelReleaseRepository
import de.htwg.zeta.persistence.general.ModelEntityRepository
import de.htwg.zeta.persistence.general.PasswordInfoRepository
import de.htwg.zeta.persistence.general.SettingsRepository
import de.htwg.zeta.persistence.general.TimedTaskRepository
import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.persistence.general.UserRepository
import de.htwg.zeta.persistence.mongo.MongoAccessAuthorisationRepository
import de.htwg.zeta.persistence.mongo.MongoBondedTaskRepository
import de.htwg.zeta.persistence.mongo.MongoEventDrivenTaskRepository
import de.htwg.zeta.persistence.mongo.MongoFileRepository
import de.htwg.zeta.persistence.mongo.MongoFilterImageRepository
import de.htwg.zeta.persistence.mongo.MongoFilterRepository
import de.htwg.zeta.persistence.mongo.MongoGeneratorImageRepository
import de.htwg.zeta.persistence.mongo.MongoGeneratorRepository
import de.htwg.zeta.persistence.mongo.MongoLoginInfoRepository
import de.htwg.zeta.persistence.mongo.MongoLogRepository
import de.htwg.zeta.persistence.mongo.MongoMetaModelEntityRepository
import de.htwg.zeta.persistence.mongo.MongoMetaModelReleaseRepository
import de.htwg.zeta.persistence.mongo.MongoModelEntityRepository
import de.htwg.zeta.persistence.mongo.MongoPasswordInfoRepository
import de.htwg.zeta.persistence.mongo.MongoSettingsRepository
import de.htwg.zeta.persistence.mongo.MongoTimedTaskRepository
import de.htwg.zeta.persistence.mongo.MongoUserRepository
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
  def provideAccessAuthorisationRepo(connection: Future[DefaultDB]): AccessAuthorisationRepository = {
    new ActorCacheAccessAuthorisationRepository(
      new MongoAccessAuthorisationRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideBondedTaskRepo(connection: Future[DefaultDB]): BondedTaskRepository = {
    new ActorCacheBondedTaskRepository(
      new MongoBondedTaskRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideEventDrivenTaskRepo(connection: Future[DefaultDB]): EventDrivenTaskRepository = {
    new ActorCacheEventDrivenTaskRepository(
      new MongoEventDrivenTaskRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideFilterRepo(connection: Future[DefaultDB]): FilterRepository = {
    new ActorCacheFilterRepository(
      new MongoFilterRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideFilterImageRepo(connection: Future[DefaultDB]): FilterImageRepository = {
    new ActorCacheFilterImageRepository(
      new MongoFilterImageRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideGeneratorRepo(connection: Future[DefaultDB]): GeneratorRepository = {
    new ActorCacheGeneratorRepository(
      new MongoGeneratorRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideGeneratorImageRepo(connection: Future[DefaultDB]): GeneratorImageRepository = {
    new ActorCacheGeneratorImageRepository(
      new MongoGeneratorImageRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideLogRepo(connection: Future[DefaultDB]): LogRepository = {
    new ActorCacheLogRepository(
      new MongoLogRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideMetaModelEntityRepo(connection: Future[DefaultDB]): MetaModelEntityRepository = {
    new ActorCacheMetaModelEntityRepository(
      new MongoMetaModelEntityRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideMetaModelReleaseRepo(connection: Future[DefaultDB]): MetaModelReleaseRepository = {
    new ActorCacheMetaModelReleaseRepository(
      new MongoMetaModelReleaseRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideModelEntityRepo(connection: Future[DefaultDB]): ModelEntityRepository = {
    new ActorCacheModelEntityRepository(
      new MongoModelEntityRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideSettingsRepo(connection: Future[DefaultDB]): SettingsRepository = {
    new ActorCacheSettingsRepository(
      new MongoSettingsRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideTimedTaskRepo(connection: Future[DefaultDB]): TimedTaskRepository = {
    new ActorCacheTimedTaskRepository(
      new MongoTimedTaskRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideUserEntityRepo(connection: Future[DefaultDB]): UserRepository = {
    new ActorCacheUserRepository(
      new MongoUserRepository(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideFileRepo(connection: Future[DefaultDB]): FileRepository = {
    new ActorCacheFileRepository(
      new MongoFileRepository(connection),
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

}
