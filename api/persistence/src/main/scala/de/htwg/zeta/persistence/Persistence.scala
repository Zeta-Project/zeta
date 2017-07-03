package de.htwg.zeta.persistence

import java.util.UUID

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheRepository
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.persistence.mongo.MongoRepository
import de.htwg.zeta.persistence.transient.TransientTokenCache


/** Persistence. */
object Persistence {

  private val settingServer = "server"
  private val settingPort = "port"
  private val settingDb = "db"
  private val defaultServer = "localhost"
  private val defaultPort = 27017
  private val defaultDb = "zeta"

  private val config: Config = ConfigFactory.load().getConfig("zeta.mongodb")
  private val configServer: String = if (config.hasPath(settingServer)) config.getString(settingServer) else defaultServer
  private val configPort: Int = if (config.hasPath(settingPort)) config.getInt(settingPort) else defaultPort
  private val configDb: String = if (config.hasPath(settingDb)) config.getString(settingDb) else defaultDb

  /** The current implementation of the Repository. */
  val fullAccessRepository: Repository = new ActorCacheRepository(new MongoRepository(s"$configServer:$configPort", configDb))

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
