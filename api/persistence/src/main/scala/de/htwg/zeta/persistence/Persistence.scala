package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.transientCache.PersistenceTransientCacheService

/** Persistence. */
object Persistence extends App {

  /** The current implementation of the PersistenceService. */
  val service = new PersistenceTransientCacheService

}
