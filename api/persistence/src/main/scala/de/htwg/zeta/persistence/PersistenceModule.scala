package de.htwg.zeta.persistence

import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.inject.Named

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import akka.util.Timeout
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.name.Names
import com.typesafe.config.ConfigFactory
import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedEntityPersistence
import de.htwg.zeta.persistence.actorCache.ActorCacheEntityPersistence
import de.htwg.zeta.persistence.actorCache.ActorCacheFilePersistence
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.mongo.MongoEntityPersistence
import de.htwg.zeta.persistence.mongo.MongoHandler
import de.htwg.zeta.persistence.mongo.MongoFilePersistence
import de.htwg.zeta.persistence.transient.TransientTokenCache
import grizzled.slf4j.Logging
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.api.MongoDriver
import reactivemongo.api.DefaultDB

/**
 * The Guice module which wires all Silhouette dependencies.
 */
class PersistenceModule extends AbstractModule with ScalaModule with Logging {

  /**
   * Configures the module.
   */
  def configure(): Unit = {
    bind[TokenCache].to[TransientTokenCache]

    bind[AccessRestrictedEntityPersistence[MetaModelEntity]]

    bind[ActorSystem].annotatedWith(Names.named("actorCache-system")).toInstance(ActorSystem("actorCache"))
    bind[Int].annotatedWith(Names.named("actorCache-numberActorsPerType")).toInstance(10)
    bind[FiniteDuration].annotatedWith(Names.named("actorCache-cacheDuration")).toInstance(Duration(10, TimeUnit.MINUTES))
    bind[Timeout].annotatedWith(Names.named("actorCache-timeout")).toInstance(Duration(10, TimeUnit.SECONDS))
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


  @Provides @Singleton
  def provideUserEntityPersistence(
      connection: Future[DefaultDB],
      @Named("actorCache-system") system: ActorSystem,
      @Named("actorCache-numberActorsPerType") numberActorsPerType: Int,
      @Named("actorCache-cacheDuration") cacheDuration: FiniteDuration,
      @Named("actorCache-timeout") timeout: Timeout
  ): EntityPersistence[User] = new ActorCacheEntityPersistence[User](
    new MongoEntityPersistence[User](connection, MongoHandler.userHandler),
    system,
    numberActorsPerType,
    cacheDuration,
    timeout
  )

  @Provides @Singleton
  def provideMetaModelEntityPersistence(
      connection: Future[DefaultDB],
      @Named("actorCache-system") system: ActorSystem,
      @Named("actorCache-numberActorsPerType") numberActorsPerType: Int,
      @Named("actorCache-cacheDuration") cacheDuration: FiniteDuration,
      @Named("actorCache-timeout") timeout: Timeout
  ): EntityPersistence[MetaModelEntity] = new ActorCacheEntityPersistence[MetaModelEntity](
    new MongoEntityPersistence[MetaModelEntity](connection, MongoHandler.metaModelEntityHandler),
    system,
    numberActorsPerType,
    cacheDuration,
    timeout
  )

  @Provides @Singleton
  def provideModelEntityPersistence(
      connection: Future[DefaultDB],
      @Named("actorCache-system") system: ActorSystem,
      @Named("actorCache-numberActorsPerType") numberActorsPerType: Int,
      @Named("actorCache-cacheDuration") cacheDuration: FiniteDuration,
      @Named("actorCache-timeout") timeout: Timeout
  ): EntityPersistence[ModelEntity] = new ActorCacheEntityPersistence[ModelEntity](
    new MongoEntityPersistence[ModelEntity](connection, MongoHandler.modelEntityHandler),
    system,
    numberActorsPerType,
    cacheDuration,
    timeout
  )

  @Provides @Singleton
  def provideFilePersistence(
      connection: Future[DefaultDB],
      @Named("actorCache-system") system: ActorSystem,
      @Named("actorCache-numberActorsPerType") numberActorsPerType: Int,
      @Named("actorCache-cacheDuration") cacheDuration: FiniteDuration,
      @Named("actorCache-timeout") timeout: Timeout
  ): FilePersistence = new ActorCacheFilePersistence (
    new MongoFilePersistence(connection),
    system,
    numberActorsPerType,
    cacheDuration,
    timeout
  )

}


