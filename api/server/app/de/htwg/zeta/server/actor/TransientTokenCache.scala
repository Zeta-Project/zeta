package de.htwg.zeta.server.actor

import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Cancellable
import akka.actor.Props
import de.htwg.zeta.server.actor.TransientTokenCacheActor.CleanUp
import de.htwg.zeta.server.actor.TransientTokenCacheActor.Create
import de.htwg.zeta.server.actor.TransientTokenCacheActor.Delete
import de.htwg.zeta.server.actor.TransientTokenCacheActor.Read
import grizzled.slf4j.Logging

/**
  * Transient Implementation of TokenCache.
  */
@Singleton
object  TransientTokenCacheActor {

  case class Create(id: UUID)

  case class Read(userId: UUID)


  case class Delete(iuserId: UUID)

  case object CleanUp

  def props(): Props = Props(new TransientTokenCache)
}

class TransientTokenCache extends Actor with Logging {

  private case class Token(userId: UUID, lastUse: Long)

  private val tokens: TrieMap[UUID, Token] = TrieMap.empty

  // scalastyle:ignore magic.number
  private val cleaningInterval = Duration(10, TimeUnit.MINUTES)

  private val lifeTime: Long = Duration(1, TimeUnit.HOURS).toMillis

  private val  cleanUpJob: Cancellable = context.system.scheduler.schedule(cleaningInterval, cleaningInterval,self,CleanUp)

  private def cleanUp() = {
    info("Cleaning expired tokens")
    val expired = System.currentTimeMillis - lifeTime
    tokens --= tokens.filter(n => n._2.lastUse > expired).keys
  }

  /**
    * Finds a token by its ID.
    *
    * @param id The unique token ID.
    * @return The found token or None if no token for the given ID could be found.
    */
  def read(id: UUID): Unit = {
    replyToSender(tokens.get(id) match {
      case Some(v) => Future.successful(v.userId)
      case None => Future.failed(new IllegalStateException)
    },sender)
  }

  /**
    * Saves a token.
    *
    * @param userId The userId the token is created for.
    * @return The saved token.
    */
  def create(userId: UUID): Unit = {
    val token = Token(userId, System.currentTimeMillis)
    val id = UUID.randomUUID
    tokens += (id -> token)
    replyToSender(Future(id),sender)
  }

  /**
    * Removes the token for the given ID.
    *
    * @param id The ID for which the token should be removed.
    * @return A future to wait for the process to be completed.
    */
  def delete(id: UUID): Unit = {
    tokens -= id
    replyToSender(Future(()),sender)
  }
  override def receive: Receive = {
    case Create(userId: UUID) => create(userId)
    case Read(id) => read(id)
    case Delete(id) => delete(id)
    case CleanUp => cleanUp()
  }

  private def replyToSender[T](f: Future[T], target: ActorRef): Unit = {
    f.onComplete {
      case Success(s) => target ! Success(s)

      case Failure(e) => target ! Failure(e)
    }
  }

  override def postStop(): Unit = {
    cleanUpJob.cancel()
  }
}
