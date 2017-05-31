package de.htwg.zeta.persistence.general

import java.util.UUID

import scala.concurrent.Future


/**
 * Give access to the Token object.
 */
trait TokenCache {

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */
  def read(id: UUID): Future[UUID]

  /**
   * Saves a token.
   *
   * @param userId The userId the token is created for.
   * @return The saved token.
   */
  def create(userId: UUID): Future[UUID]

  /**
   * Removes the token for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  def delete(id: UUID): Future[Unit]

}
