package de.htwg.zeta.persistence

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.typesafe.config.ConfigFactory
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedFilePersistence
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGdslProjectRepository
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedLogRepository
import de.htwg.zeta.persistence.general.AccessAuthorisationRepository
import de.htwg.zeta.persistence.general.BondedTaskRepository
import de.htwg.zeta.persistence.general.EventDrivenTaskRepository
import de.htwg.zeta.persistence.general.FileRepository
import de.htwg.zeta.persistence.general.FilterImageRepository
import de.htwg.zeta.persistence.general.FilterRepository
import de.htwg.zeta.persistence.general.GdslProjectRepository
import de.htwg.zeta.persistence.general.GeneratorImageRepository
import de.htwg.zeta.persistence.general.GeneratorRepository
import de.htwg.zeta.persistence.general.GraphicalDslInstanceRepository
import de.htwg.zeta.persistence.general.GraphicalDslReleaseRepository
import de.htwg.zeta.persistence.general.LogRepository
import de.htwg.zeta.persistence.general.LoginInfoRepository
import de.htwg.zeta.persistence.general.PasswordInfoRepository
import de.htwg.zeta.persistence.general.SettingsRepository
import de.htwg.zeta.persistence.general.TimedTaskRepository
import de.htwg.zeta.persistence.general.UserRepository
import de.htwg.zeta.persistence.mongo.MongoAccessAuthorisationRepository
import de.htwg.zeta.persistence.mongo.MongoBondedTaskRepository
import de.htwg.zeta.persistence.mongo.MongoEventDrivenTaskRepository
import de.htwg.zeta.persistence.mongo.MongoFileRepository
import de.htwg.zeta.persistence.mongo.MongoFilterImageRepository
import de.htwg.zeta.persistence.mongo.MongoFilterRepository
import de.htwg.zeta.persistence.mongo.MongoGeneratorImageRepository
import de.htwg.zeta.persistence.mongo.MongoGeneratorRepository
import de.htwg.zeta.persistence.mongo.MongoGraphicalDslInstanceRepository
import de.htwg.zeta.persistence.mongo.MongoGraphicalDslReleaseRepository
import de.htwg.zeta.persistence.mongo.MongoGraphicalDslRepository
import de.htwg.zeta.persistence.mongo.MongoLogRepository
import de.htwg.zeta.persistence.mongo.MongoLoginInfoRepository
import de.htwg.zeta.persistence.mongo.MongoPasswordInfoRepository
import de.htwg.zeta.persistence.mongo.MongoSettingsRepository
import de.htwg.zeta.persistence.mongo.MongoTimedTaskRepository
import de.htwg.zeta.persistence.mongo.MongoUserRepository
import grizzled.slf4j.Logging
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaModule
import reactivemongo.api.DefaultDB
import reactivemongo.api.MongoDriver

/**
  * The Guice module which wires all Persistence dependencies.
  */
class PersistenceModule extends AbstractModule with ScalaModule with Logging {

  /**
    * Configures the module.
    */
  override def configure(): Unit = {
    bind[AccessRestrictedFilePersistence]
    bind[AccessRestrictedGdslProjectRepository]
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
  private val emptyString = ""
  private val defaultUsername = emptyString
  private val defaultPassword = emptyString
  private val config = ConfigFactory.load()

  @Provides
  @Singleton
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
      if (username.isEmpty || password.isEmpty) emptyString else s"$username:$password@"
    }
    info(s"Mongo connection: $usernameAndPassword$configServer:$configPort") // scalastyle:ignore multiple.string.literals
    val uri = s"$usernameAndPassword$configServer:$configPort/$configDb"
    Future.fromTry(MongoDriver().connection(uri)).flatMap(_.database(configDb))
  }



  @Provides
  @Singleton
  def provideAccessAuthorisationRepo(connection: Future[DefaultDB]): AccessAuthorisationRepository = {
    new MongoAccessAuthorisationRepository(connection)
  }

  @Provides
  @Singleton
  def provideBondedTaskRepo(connection: Future[DefaultDB]): BondedTaskRepository = {
    new MongoBondedTaskRepository(connection)
  }

  @Provides
  @Singleton
  def provideEventDrivenTaskRepo(connection: Future[DefaultDB]): EventDrivenTaskRepository = {
    new MongoEventDrivenTaskRepository(connection)
  }

  @Provides
  @Singleton
  def provideFilterRepo(connection: Future[DefaultDB]): FilterRepository = {
    new MongoFilterRepository(connection)
  }

  @Provides
  @Singleton
  def provideFilterImageRepo(connection: Future[DefaultDB]): FilterImageRepository = {
    new MongoFilterImageRepository(connection)
  }

  @Provides
  @Singleton
  def provideGeneratorRepo(connection: Future[DefaultDB]): GeneratorRepository = {
    new MongoGeneratorRepository(connection)
  }

  @Provides
  @Singleton
  def provideGeneratorImageRepo(connection: Future[DefaultDB]): GeneratorImageRepository = {
    new MongoGeneratorImageRepository(connection)
  }

  @Provides
  @Singleton
  def provideLogRepo(connection: Future[DefaultDB]): LogRepository = {
    new MongoLogRepository(connection)
  }

  @Provides
  @Singleton
  def provideGraphicalDslRepo(connection: Future[DefaultDB]): GdslProjectRepository = {
    new MongoGraphicalDslRepository(connection)
  }

  @Provides
  @Singleton
  def provideGraphicalDslReleaseRepo(connection: Future[DefaultDB]): GraphicalDslReleaseRepository = {
    new MongoGraphicalDslReleaseRepository(connection)
  }

  @Provides
  @Singleton
  def provideGraphicalDslInstanceRepo(connection: Future[DefaultDB]): GraphicalDslInstanceRepository = {
    new MongoGraphicalDslInstanceRepository(connection)
  }

  @Provides
  @Singleton
  def provideSettingsRepo(connection: Future[DefaultDB]): SettingsRepository = {
    new MongoSettingsRepository(connection)
  }

  @Provides
  @Singleton
  def provideTimedTaskRepo(connection: Future[DefaultDB]): TimedTaskRepository = {
    new MongoTimedTaskRepository(connection)
  }

  @Provides
  @Singleton
  def provideUserEntityRepo(connection: Future[DefaultDB]): UserRepository = {
    new MongoUserRepository(connection)
  }

  @Provides
  @Singleton
  def provideFileRepo(connection: Future[DefaultDB]): FileRepository = {
    new MongoFileRepository(connection)
  }

  @Provides
  @Singleton
  def provideLoginInfoRepo(connection: Future[DefaultDB]): LoginInfoRepository = {
    new MongoLoginInfoRepository(connection)
  }

  @Provides
  @Singleton
  def providePasswordInfoRepo(connection: Future[DefaultDB]): PasswordInfoRepository = {
    new MongoPasswordInfoRepository(connection)
  }

}
