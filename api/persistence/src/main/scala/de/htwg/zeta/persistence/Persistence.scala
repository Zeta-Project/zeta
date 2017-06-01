package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.transientCache.PersistenceTransientCacheService
import de.htwg.zeta.persistence.transientCache.TransientTokenCache


/** Persistence. */
object Persistence extends App {

  /** The current implementation of the PersistenceService. */
  lazy val service = new PersistenceTransientCacheService

  /** The current implementation of the TokenCache. */
  lazy val tokenCache = new TransientTokenCache

}
