package models.persistence

import java.util.concurrent.TimeUnit

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import akka.actor.Cancellable
import akka.actor.Props
import models.document.Document
import models.persistence.DocumentAccessor.CleanUp
import models.persistence.DocumentAccessorManager.GetAccessor
import models.persistence.DocumentAccessorManager.GetAllIds


/** Manages all DocumentAccessors of type [[T]].
 *
 * @tparam T type of the document Describe param
 */
class DocumentAccessorManager[T <: Document] extends Actor { // scalastyle:ignore

  private val persistence: Persistence[T] = new CachePersistence[T] // TODO inject

  private val cleanUpJob: Cancellable = {
    val cleanUpInterval = Duration(1, TimeUnit.MINUTES) // TODO inject Duration
    context.system.scheduler.schedule(cleanUpInterval, cleanUpInterval, self, CleanUp)
  }

  /** Process received message.
   *
   * @return Receive
   */
  override def receive: Receive = {

    case GetAccessor(id) =>
      sender ! context.child(id).getOrElse{
        context.actorOf(Props(new DocumentAccessor[T](persistence)), id)
      }

    case GetAllIds =>
      val originalSender = sender
      Future {
        originalSender ! persistence.readAllIds
      }

    case CleanUp =>
      context.children.foreach(_.forward(CleanUp))

  }

  /** Process actor stopping. */
  override def postStop(): Unit = {
    cleanUpJob.cancel()
  }

}

/** Companion object of [[DocumentAccessorManager]]. */
object DocumentAccessorManager {

  /** Get an [[DocumentAccessor]] by id.
   *
   * @param id id of the document
   */
  case class GetAccessor(id: String)

  case object GetAllIds

}

