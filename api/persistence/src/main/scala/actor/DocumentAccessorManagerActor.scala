package actor

import java.util.concurrent.TimeUnit

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration

import akka.actor.Actor
import akka.actor.Cancellable
import models.document.Document
import actor.DocumentAccessorActor.CleanUp
import actor.DocumentAccessorManagerActor.CacheDuration
import actor.DocumentAccessorManagerActor.GetAccessor
import actor.DocumentAccessorManagerActor.GetAllIds
import persistence.CachePersistence
import persistence.Persistence


/** Manages all DocumentAccessors of type [[T]].
 *
 * @tparam T type of the document Describe param
 */
class DocumentAccessorManagerActor[T <: Document] extends Actor { // scalastyle:ignore


  // TODO inject
  private val cacheDuration: CacheDuration = {
    val keepInCacheTime: Long = Duration(1, TimeUnit.HOURS).toMillis
    val keepActorAliveTime: Long = Duration(1, TimeUnit.DAYS).toMillis
    val keepActorAliveAfterDeleteTime: Long = Duration(1, TimeUnit.MINUTES).toMillis
    val cleanUpInterval: FiniteDuration = Duration(1, TimeUnit.MINUTES)

    CacheDuration(cleanUpInterval, keepInCacheTime, keepActorAliveTime, keepActorAliveAfterDeleteTime)
  }

  private val persistence: Persistence[T] = new CachePersistence[T] // TODO inject
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
   * @param keepInCacheTime               how long the [[Document]] will be cached for
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


