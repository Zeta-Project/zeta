package de.htwg.zeta.persistence

import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedRepository
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.persistence.transientCache.TransientLoginInfoPersistence
import de.htwg.zeta.persistence.transientCache.TransientRepository
import de.htwg.zeta.persistence.transientCache.TransientTokenCache
import models.User


/** Persistence. */
object Persistence extends App {

  /** The current implementation of the PersistenceService. */
  lazy val service: Repository = new TransientRepository

  /** The current implementation of the PersistenceService with a overlaying Access-Restriction Layer.
   *
   * @param user The assigned user to the restriction
   * @return PersistenceService
   */
  def restrictedRepository(user: User): Repository = {
    AccessRestrictedRepository(user, service)
  }

  /** The current implementation of TokenCache. */
  lazy val tokenCache: TokenCache = new TransientTokenCache

  /** The current implementation of LoginInfoPersistence. */
  lazy val loginInfoPersistence: LoginInfoPersistence = new TransientLoginInfoPersistence

}
