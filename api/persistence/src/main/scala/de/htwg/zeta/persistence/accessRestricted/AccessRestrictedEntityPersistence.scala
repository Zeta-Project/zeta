package de.htwg.zeta.persistence.accessRestricted

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.TypeTag

import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.persistence.general.EntityPersistence

/** Persistence-Layer to restrict the access to the entity-persistence.
 *
 * @param ownerId             the user-id of the owner
 * @param accessAuthorisation accessAuthorisation
 * @param underlaying         The underlaying Persistence
 * @tparam E type of the entity
 * @param manifest implicit manifest of the entity type
 */
class AccessRestrictedEntityPersistence[E <: Entity: TypeTag]( // scalastyle:ignore
    ownerId: UUID,
    accessAuthorisation: EntityPersistence[AccessAuthorisation],
    underlaying: EntityPersistence[E])(implicit manifest: Manifest[E]) extends EntityPersistence[E] {

  /** Create a new entity.
   *
   * @param entity the entity to save
   * @return Future, which can fail
   */
  override def create(entity: E): Future[E] = {
    underlaying.create(entity).flatMap(entity =>
      accessAuthorisation.createOrUpdate(
        ownerId,
        _.grantEntityAccess(entityTypeName, entity.id),
        AccessAuthorisation(ownerId, Map.empty, Map.empty).grantEntityAccess(entityTypeName, entity.id)
      ).flatMap { _ =>
        Future.successful(entity)
      }
    )
  }

  /** Get a single entity.
   *
   * @param id The id of the entity
   * @return Future which resolve with the entity and can fail
   */
  override def read(id: UUID): Future[E] = {
    restricted(id, underlaying.read(id))
  }

  /** Update a entity.
   *
   * @param id           The id of the entity
   * @param updateEntity Function, to build the updated entity from the existing
   * @return Future containing the updated entity
   */
  override def update(id: UUID, updateEntity: (E) => E): Future[E] = {
    restricted(id, underlaying.update(id, updateEntity))
  }

  /** Delete a entity.
   *
   * @param id The id of the entity to delete
   * @return Future, which can fail
   */
  override def delete(id: UUID): Future[Unit] = {
    restricted(id, underlaying.delete(id).flatMap(_ =>
      accessAuthorisation.update(ownerId, _.revokeEntityAccess(entityTypeName, id)).flatMap(_ =>
        Future.successful(())
      )
    ))
  }

  /** Get the id's of all entity.
   *
   * @return Future containing all id's of the entity type, can fail
   */
  override def readAllIds(): Future[Set[UUID]] = {
    accessAuthorisation.readOrCreate(ownerId, AccessAuthorisation(ownerId, Map.empty, Map.empty)).map(_.listEntityAccess(entityTypeName))
  }

  private def restricted[T](id: UUID, f: => Future[T]): Future[T] = {
    accessAuthorisation.readOrCreate(ownerId, AccessAuthorisation(ownerId, Map.empty, Map.empty)).map(
      _.checkEntityAccess(entityTypeName, id)).flatMap(accessGranted =>
      if (accessGranted) {
        f
      } else {
        Future.failed(new IllegalStateException(s"Access denied: ${universe.typeOf[E].toString}"))
      }
    )
  }

}
