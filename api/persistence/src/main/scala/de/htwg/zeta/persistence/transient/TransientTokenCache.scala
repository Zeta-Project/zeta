package de.htwg.zeta.persistence.transient

import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import de.htwg.zeta.persistence.general.TokenCache
import grizzled.slf4j.Logging

/**
 * TODO
 */
class TransientTokenCache extends TokenCache with Logging {

  private case class Token(userId: UUID, lastUse: Long)

  private val tokens: mutable.HashMap[UUID, Token] = mutable.HashMap.empty

  private val cleaningInterval = Duration(1, TimeUnit.MINUTES)

  private val lifeTime: Long = Duration(1, TimeUnit.HOURS).toMillis

  /** Schedule the CleanUp job. */
  ActorSystem("TransientTokenCache").scheduler.schedule(cleaningInterval, cleaningInterval) {
    info("Cleaning expired tokens")
    val expired = System.currentTimeMillis - lifeTime // scalastyle:ignore
    tokens --= tokens.filter(n => n._2.lastUse > expired).keys
  }

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */
  override def read(id: UUID): Future[UUID] = {
    Future {
      tokens(id).userId
    }
  }

  /**
   * Saves a token.
   *
   * @param userId The userId the token is created for.
   * @return The saved token.
   */
  override def create(userId: UUID): Future[UUID] = {
    val token = Token(userId, System.currentTimeMillis)
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
