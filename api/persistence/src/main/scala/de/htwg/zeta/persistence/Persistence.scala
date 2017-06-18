package de.htwg.zeta.persistence

import java.util.UUID

import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedRepository
import de.htwg.zeta.persistence.actorCache.ActorCacheRepository
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.persistence.mongo.MongoRepository
import de.htwg.zeta.persistence.transient.TransientTokenCache


/** Persistence. */
object Persistence {

  /** The current implementation of the Repository. */
  val fullAccessRepository: Repository = new ActorCacheRepository(new MongoRepository("localhost:27017", "zeta"))

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
