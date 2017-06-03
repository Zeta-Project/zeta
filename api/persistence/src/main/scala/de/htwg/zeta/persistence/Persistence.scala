package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedPersistenceService
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.PersistenceService
import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.persistence.transientCache.TransientLoginInfoPersistence
import de.htwg.zeta.persistence.transientCache.TransientPersistenceService
import de.htwg.zeta.persistence.transientCache.TransientTokenCache
import models.User


/** Persistence. */
object Persistence extends App {

  /** The current implementation of the PersistenceService. */
  lazy val service: PersistenceService = new TransientPersistenceService

  /** The current implementation of the PersistenceService with a overlaying Access-Restriction Layer.
   *
   * @param user The assigned user to the restriction
   * @return PersistenceService
   */
  def restrictedService(user: User): PersistenceService = {
    AccessRestrictedPersistenceService(user, service)
  }

  /** The current implementation of TokenCache. */
  lazy val tokenCache: TokenCache = new TransientTokenCache

  /** The current implementation of LoginInfoPersistence. */
  lazy val loginInfoPersistence: LoginInfoPersistence = new TransientLoginInfoPersistence

}
