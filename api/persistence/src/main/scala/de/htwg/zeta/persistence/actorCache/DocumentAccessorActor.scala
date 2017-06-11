package de.htwg.zeta.persistence.actorCache

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Props
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.DocumentAccessorReceivedMessage
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.CleanUp
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.ReadDocument
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.CreateDocument
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.UpdateDocument
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.DeleteDocument
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.CreatingDocumentFailed
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.CreatingDocumentSucceed
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.ReadingDocumentSucceed
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.ReadingDocumentFailed
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.UpdatingDocumentSucceed
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.UpdatingDocumentFailed
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.DeletingDocumentSucceed
import de.htwg.zeta.persistence.actorCache.DocumentAccessorActor.DeletingDocumentFailed
import de.htwg.zeta.persistence.actorCache.DocumentAccessorManagerActor.CacheDuration
import de.htwg.zeta.persistence.general.Persistence
import models.entity.Entity


/** Access object for a single Document.
 *
 * @tparam T type of the document
 */
class DocumentAccessorActor[T <: Entity](private val persistence: Persistence[T], private val cacheDuration: CacheDuration)
  extends Actor with ActorLogging { // scalastyle:ignore

  private var actorLifeExpireTime: Long = System.currentTimeMillis() + cacheDuration.keepActorAliveTime // scalastyle:ignore


  private trait StatefulReceive extends Receive {
    override def isDefinedAt(x: Any): Boolean = x.isInstanceOf[DocumentAccessorReceivedMessage]

    override def apply(v: Any): Unit = {
      v match {
        case CleanUp => statefulCleanUp()
        case ReadDocument => statefulRead()
        case CreateDocument(doc: T) => statefulCreate(doc)
        case UpdateDocument(doc: T) => updateDocument(doc)
        case DeleteDocument => deleteDocument()
      }
    }

    def statefulCreate(doc: T): Unit

    def statefulCleanUp(): Unit

    def statefulRead(): Unit
  }


  private case class CacheState(doc: T) extends StatefulReceive {
    override def statefulCleanUp(): Unit = cleanUpInCacheState(actorLifeExpireTime)

    override def statefulRead(): Unit = readDocumentInCacheState(doc)

    override def statefulCreate(doc: T): Unit = createDocumentInCacheState()
  }

  private object CleanState extends StatefulReceive {
    override def statefulCleanUp(): Unit = cleanUpInCleanState(actorLifeExpireTime)

    override def statefulRead(): Unit = readDocumentInCleanState()

    override def statefulCreate(doc: T): Unit = createDocumentInCleanState(doc)
  }


  /** Process received messages.
   *
   * @return Receive
   */
  override def receive: Receive = {
    CleanState
  }

  private lazy val id: UUID = {
    UUID.fromString(context.self.path.name)
  }


  private def becomeCleanState(keepAliveTime: Long = cacheDuration.keepActorAliveTime): Unit = {
    this.actorLifeExpireTime = System.currentTimeMillis + keepAliveTime
    context.unbecome()
  }

  private def becomeCacheState(doc: T): Unit = {
    this.actorLifeExpireTime = System.currentTimeMillis + cacheDuration.keepInCacheTime
    context.become(CacheState(doc), discardOld = true)
  }

  private def cleanUpInCleanState(actorLifeExpireTime: Long): Unit = {
    if (System.currentTimeMillis > actorLifeExpireTime) {
      context.stop(self)
    }
  }

  private def cleanUpInCacheState(cacheExpireTime: Long): Unit = {
    if (System.currentTimeMillis > cacheExpireTime) {
      becomeCleanState()
    }
  }

  private def createDocumentInCacheState(): Unit = {
    sender ! CreatingDocumentFailed(DocumentAccessorActor.documentExists)
  }

  private def createDocumentInCleanState(doc: T): Unit = {
    if (id == doc.id) {
      persistence.create(doc) onComplete {
        case Success(_) =>
          becomeCacheState(doc)
          sender ! CreatingDocumentSucceed
        case Failure(e) =>
          sender ! CreatingDocumentFailed(e.getMessage)
      }
    } else {
      sender ! CreatingDocumentFailed(DocumentAccessorActor.differentIDs)
    }
  }

  private def readDocumentInCleanState(): Unit = {
    persistence.read(id) onComplete {
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
    if (id == doc.id) {
      persistence.update(doc.id, _ => doc) onComplete {
        case Success(_) =>
          sender ! UpdatingDocumentSucceed
          becomeCacheState(doc)
        case Failure(e) =>
          sender ! UpdatingDocumentFailed(e.getMessage)
      }
    } else {
      sender ! UpdatingDocumentFailed(DocumentAccessorActor.differentIDs)
    }
  }

  private def deleteDocument(): Unit = {
    persistence.delete(id) onComplete {
      case Success(_) =>
        sender ! DeletingDocumentSucceed
        becomeCleanState(cacheDuration.keepActorAliveAfterDeleteTime)
      case Failure(e) =>
        sender ! DeletingDocumentFailed(e.getMessage)
    }
  }


}

/**
 * Companion object for the DocumentAccessor.
 */
object DocumentAccessorActor {

  private val differentIDs = "id's do not match"
  private val documentExists = "document already exists"

  /** Marker trait for all messages received by DocumentAccessor */
  sealed trait DocumentAccessorReceivedMessage

  /** Request-Message: Invoke the cleaning process. */
  private[actorCache] case object CleanUp extends DocumentAccessorReceivedMessage

  /** Request-Message: Create the document.
   *
   * @param doc the document to create
   */
  case class CreateDocument(doc: Entity) extends DocumentAccessorReceivedMessage

  /** Response-Message: Creating of the document succeeded. */
  case object CreatingDocumentSucceed

  /** Response-Message: Creating of the document failed.
   *
   * @param error the error message
   */
  case class CreatingDocumentFailed(error: String)

  /** Request-Message: Read the document. */
  case object ReadDocument extends DocumentAccessorReceivedMessage

  /** Response-Message: Reading of the document succeeded.
   *
   * @param doc the document
   * @tparam T the type of the document
   */
  case class ReadingDocumentSucceed[T <: Entity](doc: T) // scalastyle:ignore

  /** Response-Message: Reading of the document failed.
   *
   * @param error the error message
   */
  case class ReadingDocumentFailed(error: String)

  /** Update the document.
   *
   * @param doc the document to update
   */
  case class UpdateDocument(doc: Entity) extends DocumentAccessorReceivedMessage

  /** Response-Message: Updating of the document succeeded. */
  case object UpdatingDocumentSucceed

  /** Response-Message: Updating of the document failed.
   *
   * @param error the error message
   */
  case class UpdatingDocumentFailed(error: String)

  /** Request-Message: Delete the document. */
  case object DeleteDocument extends DocumentAccessorReceivedMessage

  /** Response-Message: Deleting of the document succeeded. */
  case object DeletingDocumentSucceed

  /** Response-Message: Deleting of the document failed.
   *
   * @param error the error message
   */
  case class DeletingDocumentFailed(error: String)

}

trait DocumentAccessorFactory {
  def props[T <: Entity](persistence: Persistence[T], cacheDuration: CacheDuration): Props
}

object DocumentAccessorFactoryDefaultImpl extends DocumentAccessorFactory {
  override def props[T <: Entity](persistence: Persistence[T], cacheDuration: CacheDuration): Props = {
    Props(new DocumentAccessorActor[T](persistence, cacheDuration))
  }
}
