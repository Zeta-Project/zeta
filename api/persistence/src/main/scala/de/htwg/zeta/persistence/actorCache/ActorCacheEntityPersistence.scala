package de.htwg.zeta.persistence.actorCache

import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.routing.ConsistentHashingPool
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Create
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Delete
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Read
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Update
import de.htwg.zeta.persistence.general.EntityPersistence

/**
 * TODO.
 */
class ActorCacheEntityPersistence[E <: Entity](
    system: ActorSystem,
    underlying: EntityPersistence[E],
    nrOfInstances: Int,
    cacheDuration: FiniteDuration
) extends EntityPersistence[E] {

  def hashMapping: ConsistentHashMapping = {
    case Create(entity) => entity.id
    case Read(id) => id
    case Update(id, _) => id
    case Delete(id) => id
  }

  private val router: ActorRef = system.actorOf(
    ConsistentHashingPool(
      nrOfInstances = nrOfInstances,
      hashMapping = hashMapping
    ).props(
      EntityCacheActor.props(underlying, Duration.Zero)
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
   * @param entity The updated entity
   * @return Future containing the updated entity
   */
  override private[persistence] def update(entity: E): Future[E] = {
    ???
  }

  /** Delete a entity.
   *
   * @param id The id of the entity to delete
   * @return Future
   */
  override def delete(id: UUID): Future[Unit] = {
    ???
  }

  /** Get the id's of all entity.
   *
   * @return Future containing all id's of the entity type
   */
  override def readAllIds(): Future[Set[UUID]] = {
    underlying.readAllIds()
  }


}
