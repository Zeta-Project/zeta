package de.htwg.zeta.persistence.transientCache

import java.util.UUID

import scala.collection.mutable
import scala.concurrent.Future

import de.htwg.zeta.persistence.general.TokenCache
import org.joda.time.DateTime

/**
 * TODO
 */
class TransientTokenCache extends TokenCache {

  // TODO clean expired tokens regularly

  private case class Token(userId: UUID, expiry: DateTime)

  private val tokens: mutable.HashMap[UUID, Token] = mutable.HashMap.empty

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */
  override def read(id: UUID): Future[UUID] = Future{
    tokens(id).userId
  }

  /**
   * Saves a token.
   *
   * @param userId The userId the token is created for.
   * @return The saved token.
   */
  override def create(userId: UUID): Future[UUID] = {
    val token = Token(userId, DateTime.now.plusHours(1))
    val id = UUID.randomUUID
    tokens += (id -> token)
    Future.successful(id)
  }

  /**
   * Removes the token for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  override def delete(id: UUID): Future[Unit] = {
    tokens -= id
    Future.successful(())
  }

}
