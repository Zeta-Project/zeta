package de.htwg.zeta.persistence

import java.util.UUID

import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedRepository
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.PasswordInfoPersistence
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.general.TokenCache
import de.htwg.zeta.persistence.mongo.MongoRepository
import de.htwg.zeta.persistence.transient.TransientLoginInfoPersistence
import de.htwg.zeta.persistence.transient.TransientPasswordInfoPersistence
import de.htwg.zeta.persistence.transient.TransientTokenCache


/** Persistence. */
object Persistence extends App {

  /** The current implementation of the PersistenceService. */
  lazy val fullAccessRepository: Repository = new MongoRepository("localhost:27017", "zeta")

  /** The current implementation of the PersistenceService with a overlaying Access-Restriction Layer.
   *
   * @param ownerID The id of the assigned user to the restriction
   * @return PersistenceService
   */
  def restrictedAccessRepository(ownerID: UUID): Repository = {
    AccessRestrictedRepository(ownerID, fullAccessRepository)
  }

  /** The current implementation of TokenCache. */
  lazy val tokenCache: TokenCache = new TransientTokenCache

  /** The current implementation of LoginInfoPersistence. */
  lazy val loginInfoPersistence: LoginInfoPersistence = new TransientLoginInfoPersistence

  /** The current implementation of PasswordInfoPersistence. */
  lazy val passwordInfoPersistence: PasswordInfoPersistence = new TransientPasswordInfoPersistence

}
