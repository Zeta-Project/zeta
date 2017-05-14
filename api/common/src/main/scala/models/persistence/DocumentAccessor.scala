package models.persistence

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration

import akka.actor.Actor
import models.document.Document
import models.persistence.DocumentAccessor.CleanUp
import models.persistence.DocumentAccessor.ReadDocument
import models.persistence.DocumentAccessor.CreateDocument
import models.persistence.DocumentAccessor.UpdateDocument
import models.persistence.DocumentAccessor.DeleteDocument

/** Access object for a single Document.
 *
 * @tparam D type of the document
 */
class DocumentAccessor[D <: Document](persistence: Persistence[D]) extends Actor { // scalastyle:ignore


  private val keepInCacheTime: Long = Duration(1, TimeUnit.HOURS).toMillis // TODO inject
  private val keepActorAliveTime: Long = Duration(1, TimeUnit.DAYS).toMillis // TODO inject


  /** Process received messages.
   *
   * @return Receive
   */
  override def receive: Receive = {
    cleanState(System.currentTimeMillis)
  }


  private def id = context.self.path.name

  private def cleanState(actorLifeExpireTime: Long): Receive = {


    case CleanUp =>
      if (System.currentTimeMillis > actorLifeExpireTime) {
        context.stop(self)
      }

    case CreateDocument(doc) =>
      // TODO

    case ReadDocument =>
    // TODO

    case UpdateDocument(doc) =>
    // TODO

    case DeleteDocument =>
    // TODO

  }

  private def cacheState(document: D, cacheExpireTime: Long): Receive = {
    case CleanUp =>
      if (System.currentTimeMillis > cacheExpireTime) {
        context.become(cleanState(System.currentTimeMillis + keepActorAliveTime))
      }

    case CreateDocument(doc) =>
    // TODO

    case ReadDocument =>
    // TODO

    case UpdateDocument(doc) =>
    // TODO

    case DeleteDocument =>
    // TODO
  }

}

/**
 * Companion object for [[DocumentAccessor]].
 */
object DocumentAccessor {

  /** Message to invoke the cleaning. */
  private[persistence] case object CleanUp

  /** Create a document.
   *
   * @param doc the document to create
   */
  case class CreateDocument(doc: Document)

  /** Read the document. */
  case object ReadDocument

  /** Update the document.
   *
   * @param doc the document to update
   */
  case class UpdateDocument(doc: Document)

  /** Delete the document. */
  case object DeleteDocument


}
