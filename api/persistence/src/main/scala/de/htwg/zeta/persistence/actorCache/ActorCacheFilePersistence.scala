package de.htwg.zeta.persistence.actorCache

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.routing.ConsistentHashingPool
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import akka.util.Timeout
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Create
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Delete
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Read
import de.htwg.zeta.persistence.actorCache.FileCacheActor.Update
import de.htwg.zeta.persistence.general.FilePersistence

/**
 * Actor Cache Implementation of FilePersistence.
 */
class ActorCacheFilePersistence(
    system: ActorSystem,
    underlying: FilePersistence,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    implicit val timeout: Timeout
) extends FilePersistence {

  private def hashMapping: ConsistentHashMapping = {
    case Create(file) => file.id.toString
    case Read(id, _) => id.toString
    case Update(file) => file.id.toString
    case Delete(id, _) => id.toString
  }

  private val router: ActorRef = system.actorOf(
    ConsistentHashingPool(
      nrOfInstances = numberActorsPerEntityType,
      hashMapping = hashMapping
    ).props(
      FileCacheActor.props(underlying, cacheDuration)
    ),
    "File"
  )

  /** Create a new file.
   *
   * @param file the file to save
   * @return Future, with the created file
   */
  override def create(file: File): Future[File] = {
    (router ? Create(file)).flatMap {
      case Success(file: File) => Future.successful(file)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Read a file.
   *
   * @param id   the id of the file
   * @param name the name of the file
   * @return Future containing the read file
   */
  override def read(id: UUID, name: String): Future[File] = {
    (router ? Read(id, name)).flatMap {
      case Success(file: File) => Future.successful(file)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Update a file.
   *
   * @param file The updated file
   * @return Future containing the updated file
   */
  override def update(file: File): Future[File] = {
    (router ? Update(file)).flatMap {
      case Success(file: File) => Future.successful(file)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Delete a file.
   *
   * @param id   The id of the file to delete
   * @param name the name of the file
   * @return Future
   */
  override def delete(id: UUID, name: String): Future[Unit] = {
    (router ? Delete(id, name)).flatMap {
      case Success(Unit) => Future.successful(())
      case Failure(e) => Future.failed(e)
    }
  }

  /** Get the id's of all file.
   *
   * @return Future containing all id's of the file type
   */
  override def readAllKeys(): Future[Map[UUID, Set[String]]] = {
    underlying.readAllKeys()
  }

}
