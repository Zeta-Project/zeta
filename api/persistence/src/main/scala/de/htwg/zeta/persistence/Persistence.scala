package de.htwg.zeta.persistence

import java.util.UUID

import com.typesafe.config.ConfigFactory
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedRepository
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.persistence.mongo.MongoRepository
import de.htwg.zeta.persistence.transient.TransientTokenCache


/** Persistence. */
object Persistence {

  private val SettingServer = "server"
  private val SettingPort = "port"
  private val SettingDb = "db"
  private val DefaultServer = "localhost"
  private val DefaultPort = 27017
  private val DefaultDb = "zeta"

  private val config = ConfigFactory.load().getConfig("mongodb")
  private val configServer = if (config.hasPath(SettingServer)) config.getString(SettingServer) else DefaultServer
  private val configPort = if (config.hasPath(SettingPort)) config.getInt(SettingPort) else DefaultPort
  private val configDb = if (config.hasPath(SettingDb)) config.getString(SettingDb) else DefaultDb

  /** The current implementation of the Repository. */
  val fullAccessRepository: Repository = new MongoRepository(s"$configServer:$configPort", configDb)

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
