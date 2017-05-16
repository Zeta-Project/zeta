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
    case CleanUp => cleanUpInCleanState(actorLifeExpireTime)
    case CreateDocument(doc: T) => createDocument(doc)
    case ReadDocument => readDocumentInCleanState()
    case UpdateDocument(doc: T) => updateDocument(doc)
    case DeleteDocument => deleteDocument()
  }

  private def cacheState(doc: T, actorLifeExpireTime: Long): Receive = {
    case CleanUp => cleanUpInCacheState(actorLifeExpireTime)
    case CreateDocument(doc: T) => createDocument(doc)
    case ReadDocument => readDocumentInCacheState(doc)
    case UpdateDocument(doc: T) => updateDocument(doc)
    case DeleteDocument => deleteDocument()
  }

  private def cleanUpInCleanState(actorLifeExpireTime: Long): Unit = {
    if (System.currentTimeMillis > actorLifeExpireTime) {
      context.stop(self)
    }
  }

  private def cleanUpInCacheState(cacheExpireTime: Long): Unit = {
    if (System.currentTimeMillis > cacheExpireTime) {
      context.become(cleanState(System.currentTimeMillis + keepActorAliveTime))
    }
  }

  private def createDocument(doc: T): Unit = {
    Try(persistence.create(doc)) match {
      case Success(_) =>
        becomeCacheState(doc)
        sender ! CreatingDocumentSucceed
      case Failure(e) =>
        sender ! CreatingDocumentFailed(e.getMessage)
    }
  }

  private def readDocumentInCleanState(): Unit = {
    Try(persistence.read(id)) match {
      case Success(doc) =>
        becomeCacheState(doc)
        sender ! ReadingDocumentSucceed(doc)
      case Failure(e) =>
        sender ! ReadingDocumentFailed(e.getMessage)
    }
  }

  private def readDocumentInCacheState(doc: T): Unit = {
    becomeCacheState(doc)
    sender ! ReadingDocumentSucceed(doc)
  }

  private def updateDocument(doc: T): Unit = {
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

  private def deleteDocument(): Unit = {
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
 * Companion object for the DocumentAccessor.
 */
object DocumentAccessor {

  /** Request-Message: Invoke the cleaning process. */
  private[persistence] case object CleanUp

  /** Request-Message: Create the document.
   *
   * @param doc the document to create
   */
  case class CreateDocument(doc: Document)

  /** Response-Message: Creating of the document succeeded. */
  case object CreatingDocumentSucceed

  /** Response-Message: Creating of the document failed.
   *
   * @param error the error message
   */
  case class CreatingDocumentFailed(error: String)

  /** Request-Message: Read the document. */
  case object ReadDocument

  /** Response-Message: Reading of the document succeeded.
   *
   * @param doc the document
   * @tparam T the type of the document
   */
  case class ReadingDocumentSucceed[T <: Document](doc: T) // scalastyle:ignore

  /** Response-Message: Reading of the document failed.
   *
   * @param error the error message
   */
  case class ReadingDocumentFailed(error: String)

  /** Update the document.
   *
   * @param doc the document to update
   */
  case class UpdateDocument(doc: Document)

  /** Response-Message: Updating of the document succeeded. */
  case object UpdatingDocumentSucceed

  /** Response-Message: Updating of the document failed.
   *
   * @param error the error message
   */
  case class UpdatingDocumentFailed(error: String)

  /** Request-Message: Delete the document. */
  case object DeleteDocument

  /** Response-Message: Deleting of the document succeeded. */
  case object DeletingDocumentSucceed

  /** Response-Message: Deleting of the document failed.
   *
   * @param error the error message
   */
  case class DeletingDocumentFailed(error: String)

}
