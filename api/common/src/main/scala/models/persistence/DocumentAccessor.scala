package models.persistence

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import akka.actor.Actor
import akka.actor.ActorLogging
import models.document.Document
import models.persistence.DocumentAccessor.CleanUp
import models.persistence.DocumentAccessor.CreateDocument
import models.persistence.DocumentAccessor.CreatingDocumentFailed
import models.persistence.DocumentAccessor.CreatingDocumentSucceed
import models.persistence.DocumentAccessor.DeleteDocument
import models.persistence.DocumentAccessor.DeletingDocumentFailed
import models.persistence.DocumentAccessor.DeletingDocumentSucceed
import models.persistence.DocumentAccessor.ReadDocument
import models.persistence.DocumentAccessor.ReadingDocumentFailed
import models.persistence.DocumentAccessor.ReadingDocumentSucceed
import models.persistence.DocumentAccessor.UpdateDocument
import models.persistence.DocumentAccessor.UpdatingDocumentFailed
import models.persistence.DocumentAccessor.UpdatingDocumentSucceed

/** Access object for a single Document.
 *
 * @tparam T type of the document
 */
class DocumentAccessor[T <: Document](persistence: Persistence[T]) extends Actor with ActorLogging { // scalastyle:ignore


  private val keepInCacheTime: Long = Duration(1, TimeUnit.HOURS).toMillis // TODO inject
  private val keepActorAliveTime: Long = Duration(1, TimeUnit.DAYS).toMillis // TODO inject


  /** Process received messages.
   *
   * @return Receive
   */
  override def receive: Receive = {
    cleanState(System.currentTimeMillis + keepInCacheTime)
  }

  private def id: String = {
    context.self.path.name
  }

  private def cleanState(actorLifeExpireTime: Long): Receive = {
    cleanUpInCleanState(actorLifeExpireTime).
      orElse(createDocument()).
      orElse(readInCleanState()).
      orElse(updateDocument()).
      orElse(deleteDocument())
  }

  private def cacheState(doc: T, cacheExpireTime: Long): Receive = {
    cleanUpInCacheState(cacheExpireTime).
      orElse(createDocument()).
      orElse(readInCacheState(doc)).
      orElse(updateDocument()).
      orElse(deleteDocument())
  }


  private def cleanUpInCleanState(actorLifeExpireTime: Long): Receive = {
    case CleanUp =>
      if (System.currentTimeMillis > actorLifeExpireTime) {
        context.stop(self)
      }
  }

  private def cleanUpInCacheState(cacheExpireTime: Long): Receive = {
    case CleanUp =>
      if (System.currentTimeMillis > cacheExpireTime) {
        context.become(cleanState(System.currentTimeMillis + keepActorAliveTime))
      }
  }

  private def createDocument(): Receive = {
    case CreateDocument(doc: T) =>
      Try(persistence.create(doc)) match {
        case Success(_) =>
          sender ! CreatingDocumentSucceed
          becomeCacheState(doc)
        case Failure(e) =>
          sender ! CreatingDocumentFailed(e.getMessage)
      }
  }

  private def readInCleanState(): Receive = {
    case ReadDocument =>
      Try(persistence.read(id)) match {
        case Success(doc) =>
          sender ! ReadingDocumentSucceed(doc)
          becomeCacheState(doc)
        case Failure(e) =>
          sender ! ReadingDocumentFailed(e.getMessage)
      }
  }

  private def readInCacheState(doc: T): Receive = {
    case ReadDocument =>
      sender ! ReadingDocumentSucceed(doc)
      becomeCacheState(doc)
  }

  private def updateDocument(): Receive = {
    case UpdateDocument(doc: T) =>
      if (id == doc.id()) {
        Try(persistence.update(doc)) match {
          case Success(_) =>
            sender ! UpdatingDocumentSucceed
            becomeCacheState(doc)
          case Failure(e) =>
            sender ! UpdatingDocumentFailed(e.getMessage)
        }
      } else {
        sender ! UpdatingDocumentFailed("id's do not match")
      }
  }

  private def deleteDocument(): Receive = {
    case DeleteDocument =>
      Try(persistence.delete(id)) match {
        case Success(_) =>
          sender ! DeletingDocumentSucceed
          becomeCleanState()
        case Failure(e) =>
          sender ! DeletingDocumentFailed(e.getMessage)
      }
  }


  private def becomeCacheState(doc: T): Unit = {
    context.become(cacheState(doc, System.currentTimeMillis + keepInCacheTime))
  }

  private def becomeCleanState(): Unit = {
    context.become(cleanState(System.currentTimeMillis + keepActorAliveTime))
  }

}

/**
 * Companion object for [[DocumentAccessor]].
 */
object DocumentAccessor {

  /** Message to invoke the cleaning process. */
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


  case object CreatingDocumentSucceed

  case class CreatingDocumentFailed(error: String)

  case class ReadingDocumentSucceed[T <: Document](doc: T)

  case class ReadingDocumentFailed(error: String)

  case object UpdatingDocumentSucceed

  case class UpdatingDocumentFailed(error: String)

  case object DeletingDocumentSucceed

  case class DeletingDocumentFailed(error: String)


}
