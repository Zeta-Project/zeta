package de.htwg.zeta.persistence.actorCache

import java.util.concurrent.TimeUnit

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration

import akka.actor.Actor
import akka.actor.Cancellable
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.CleanUp
import de.htwg.zeta.persistence.actorCache.DocumentAccessorManagerActor.CacheDuration
import de.htwg.zeta.persistence.actorCache.DocumentAccessorManagerActor.GetAccessor
import de.htwg.zeta.persistence.actorCache.DocumentAccessorManagerActor.GetAllIds
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.transient.TransientPersistence
import models.entity.Entity


/** Manages all DocumentAccessors of type [[T]].
 *
 * @tparam T type of the document Describe param
 */
class DocumentAccessorManagerActor[T <: Entity] extends Actor { // scalastyle:ignore


  // TODO inject
  private val cacheDuration: CacheDuration = {
    val keepInCacheTime: Long = Duration(1, TimeUnit.HOURS).toMillis
    val keepActorAliveTime: Long = Duration(1, TimeUnit.DAYS).toMillis
    val keepActorAliveAfterDeleteTime: Long = Duration(1, TimeUnit.MINUTES).toMillis
    val cleanUpInterval: FiniteDuration = Duration(1, TimeUnit.MINUTES)

    CacheDuration(cleanUpInterval, keepInCacheTime, keepActorAliveTime, keepActorAliveAfterDeleteTime)
  }

  private val persistence: EntityPersistence[T] = new TransientPersistence[T] // TODO inject
  private val documentAccessorFactory: DocumentAccessorFactory = DocumentAccessorFactoryDefaultImpl // TODO inject

  private val cleanUpJob: Cancellable = {
    context.system.scheduler.schedule(cacheDuration.cleanUpInterval, cacheDuration.cleanUpInterval, self, CleanUp)(context.dispatcher)
  }

  /** Process received message.
   *
   * @return Receive
   */
  override def receive: Receive = {

    case GetAccessor(id) =>
      sender ! context.child(id).getOrElse {
        context.actorOf(documentAccessorFactory.props(persistence, cacheDuration), id)
      }

    case GetAllIds =>
      val originalSender = sender
      Future {
        originalSender ! persistence.readAllIds
      }(context.dispatcher)

    case CleanUp =>
      context.children.foreach(_.forward(CleanUp))

  }

  /** Process actor stopping. */
  override def postStop(): Unit = {
    cleanUpJob.cancel()
  }

}

/** Companion object of DocumentAccessorManager. */
object DocumentAccessorManagerActor {

  /**
   * @param cleanUpInterval               the interval between cleanup checks
   * @param keepInCacheTime               how long the [[Entity]] will be cached for
   * @param keepActorAliveTime            how long the Actor will be kept alive
   * @param keepActorAliveAfterDeleteTime how long the Actor will be kept alive after Delete has been called.
   */
  private[persistence] case class CacheDuration(
      cleanUpInterval: FiniteDuration,
      keepInCacheTime: Long,
      keepActorAliveTime: Long,
      keepActorAliveAfterDeleteTime: Long)

  /** Request-Message: Get an DocumentAccessor by id.
   *
   * @param id id of the document
   */
  case class GetAccessor(id: String)

  /** Request-Message: Get all id's of the document type. */
  case object GetAllIds

}


