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
import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Create
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Delete
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Read
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Update
import de.htwg.zeta.persistence.general.EntityPersistence

/**
 * Actor Cache Implementation of EntityPersistence.
 */
class ActorCacheEntityPersistence[E <: Entity](
    system: ActorSystem,
    underlying: EntityPersistence[E],
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    implicit val timeout: Timeout)(
    implicit manifest: Manifest[E]
) extends EntityPersistence[E] {

  private def hashMapping: ConsistentHashMapping = {
    case Create(entity) => entity.id.hashCode
    case Read(id) => id.hashCode
    case Update(id, _) => id.hashCode
    case Delete(id) => id.hashCode
  }

  private val router: ActorRef = system.actorOf(
    ConsistentHashingPool(
      nrOfInstances = numberActorsPerEntityType,
      hashMapping = hashMapping
    ).props(
      EntityCacheActor.props(underlying, cacheDuration)
    ),
    entityTypeName
  )

  /** Create a new entity.
   *
   * @param entity the entity to save
   * @return Future, with the created entity
   */
  override def create(entity: E): Future[E] = {
    (router ? Create(entity)).flatMap {
      case Success(entity: E) => Future.successful(entity)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Get a single entity.
   *
   * @param id The id of the entity
   * @return Future containing the read entity
   */
  override def read(id: UUID): Future[E] = {
    (router ? Read(id)).flatMap {
      case Success(entity: E) => Future.successful(entity)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Update a entity.
   *
   * @param id           The id of the entity
   * @param updateEntity Function, to build the updated entity from the existing
   * @return Future containing the updated entity
   */
  override def update(id: UUID, updateEntity: E => E): Future[E] = {
    (router ? Update(id, updateEntity)).flatMap {
      case Success(entity: E) => Future.successful(entity)
      case Failure(e) => Future.failed(e)
    }
  }

  /** Delete a entity.
   *
   * @param id The id of the entity to delete
   * @return Future
   */
  override def delete(id: UUID): Future[Unit] = {
    (router ? Delete(id)).flatMap {
      case Success(()) => Future.successful(())
      case Failure(e) => Future.failed(e)
    }
  }

  /** Get the id's of all entity.
   *
   * @return Future containing all id's of the entity type
   */
  override def readAllIds(): Future[Set[UUID]] = {
    underlying.readAllIds()
  }

}
