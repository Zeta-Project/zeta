package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.persistence.transientCache.PersistenceTransientCacheService
import de.htwg.zeta.persistence.transientCache.TransientTokenCache

/** Persistence. */
object Persistence extends App {

  /** The current implementation of the PersistenceService. */
  val service = new PersistenceTransientCacheService

  val tokenCache = new TransientTokenCache

}
