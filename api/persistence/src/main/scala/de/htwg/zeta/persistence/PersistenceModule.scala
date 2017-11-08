package de.htwg.zeta.persistence

import java.util.concurrent.TimeUnit
import javax.inject.Singleton

import scala.concurrent.Future
import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.typesafe.config.ConfigFactory
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.Log
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedEntityPersistence
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedFilePersistence
import de.htwg.zeta.persistence.actorCache.ActorCacheEntityPersistence
import de.htwg.zeta.persistence.actorCache.ActorCacheFilePersistence
import de.htwg.zeta.persistence.actorCache.ActorCacheLoginInfoPersistence
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.persistence.mongo.MongoEntityPersistence
import de.htwg.zeta.persistence.mongo.MongoFilePersistence
import de.htwg.zeta.persistence.mongo.MongoHandler
import de.htwg.zeta.persistence.mongo.MongoLoginInfoPersistence
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
    bind[AccessRestrictedEntityPersistence[MetaModelEntity]]
    bind[AccessRestrictedEntityPersistence[ModelEntity]]
    bind[AccessRestrictedEntityPersistence[Log]]
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
  def provideUserEntityPersistence(connection: Future[DefaultDB]): EntityPersistence[User] = {
    new ActorCacheEntityPersistence[User](
      new MongoEntityPersistence[User](connection, MongoHandler.userHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideMetaModelEntityPersistence(connection: Future[DefaultDB]): EntityPersistence[MetaModelEntity] = {
    new ActorCacheEntityPersistence(
      new MongoEntityPersistence(connection, MongoHandler.metaModelEntityHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideModelEntityPersistence(connection: Future[DefaultDB]): EntityPersistence[ModelEntity] = {
    new ActorCacheEntityPersistence(
      new MongoEntityPersistence(connection, MongoHandler.modelEntityHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideFilePersistence(connection: Future[DefaultDB]): FilePersistence = {
    new ActorCacheFilePersistence(
      new MongoFilePersistence(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideGeneratorPersistence(connection: Future[DefaultDB]): EntityPersistence[Generator] = {
    new ActorCacheEntityPersistence(
      new MongoEntityPersistence(connection, MongoHandler.generatorHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideFilterPersistence(connection: Future[DefaultDB]): EntityPersistence[Filter] = {
    new ActorCacheEntityPersistence(
      new MongoEntityPersistence(connection, MongoHandler.filterHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideGeneratorImagePersistence(connection: Future[DefaultDB]): EntityPersistence[GeneratorImage] = {
    new ActorCacheEntityPersistence(
      new MongoEntityPersistence(connection, MongoHandler.generatorImageHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideMetaModelReleasePersistence(connection: Future[DefaultDB]): EntityPersistence[MetaModelRelease] = {
    new ActorCacheEntityPersistence(
      new MongoEntityPersistence(connection, MongoHandler.metaModelReleaseHandler),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

  @Provides @Singleton
  def provideLoginInfoPersistence(connection: Future[DefaultDB]): LoginInfoPersistence = {
    new ActorCacheLoginInfoPersistence(
      new MongoLoginInfoPersistence(connection),
      system, numberActorsPerType, cacheDuration, timeout
    )
  }

}
