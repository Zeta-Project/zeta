package de.htwg.zeta.persistence

import java.util.UUID

import com.typesafe.config.ConfigFactory
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheRepository
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.persistence.mongo.MongoRepository
import de.htwg.zeta.persistence.transient.TransientTokenCache
import grizzled.slf4j.Logging


/** Persistence. */
object Persistence {

  /** The current implementation of the Repository. */
  val fullAccessRepository: Repository = new ActorCacheRepository(PersistenceMongoDb())

  /** The current implementation of the Repository with an overlying Access-Restriction Layer.
   *
   * @param ownerID The id of the assigned user to the restriction
   * @return PersistenceService
   */
  def restrictedAccessRepository(ownerID: UUID): Repository = {
    new AccessRestrictedRepository(ownerID, fullAccessRepository)
  }

  /** The current implementation of TokenCache. */
  val tokenCache: TokenCache = new TransientTokenCache

}

object PersistenceMongoDb extends Logging {

  private val settingServer = "zeta.mongodb.server"
  private val settingPort = "zeta.mongodb.port"
  private val settingDb = "zeta.mongodb.db"
  private val defaultServer = "localhost"
  private val defaultPort = "27017"
  private val defaultDb = "zeta"
  private val config = ConfigFactory.load()

  /**
   * Creates a MongoDb specific Repository instance
   * @param db Override db setting
   * @return
   */
  def apply(db: String = ""): Repository = {

    val configServer = getString(settingServer, defaultServer)
    val configPort = getInt(settingPort, defaultPort)
    val configDb = getString(settingDb, defaultDb)
    info(s"Mongo connection: $configServer:$configPort") // scalastyle:ignore multiple.string.literals
    new MongoRepository(s"$configServer:$configPort", if (db.length == 0) configDb else db)
  }

  private def getString(path: String, default: String): String = {
    if (config.hasPath(path)) config.getString(path) else default
  }

  private def getInt(path: String, default: String): String = {
    if (config.hasPath(path)) config.getInt(path).toString else default
  }
}
