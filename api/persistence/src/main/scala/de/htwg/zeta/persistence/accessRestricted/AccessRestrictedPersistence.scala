package de.htwg.zeta.persistence.accessRestricted

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.persistence.general.Persistence
import models.Entity


/** Persistence-Layer to restrict the access to the persistence.
 *
 * @param ownerId             the user-id of the owner
 * @param accessAuthorisation accessAuthorisation
 * @param underlaying         The underlaying Persistence
 * @tparam E type of the entity
 * @param manifest implicit manifest of the entity type
 */
case class AccessRestrictedPersistence[E <: Entity]( // scalastyle:ignore
    ownerId: UUID,
    accessAuthorisation: Persistence[AccessAuthorisation],
    underlaying: Persistence[E])(implicit manifest: Manifest[E]) extends Persistence[E] {

  /** Create a new entity.
   *
   * @param entity the entity to save
   * @return Future, which can fail
   */
  override def create(entity: E): Future[E] = {
    underlaying.create(entity).flatMap(entity =>
      accessAuthorisation.update(ownerId, _.grantAccess(entityTypeName, entity.id)).flatMap { _ =>
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
   * @param entity The updated entity
   * @return Future containing the updated entity
   */
  override private[persistence] def update(entity: E): Future[E] = {
    restricted(entity.id, underlaying.update(entity))
  }

  /** Delete a entity.
   *
   * @param id The id of the entity to delete
   * @return Future, which can fail
   */
  override def delete(id: UUID): Future[Unit] = {
    restricted(id, underlaying.delete(id).flatMap(_ =>
      accessAuthorisation.update(ownerId, _.revokeAccess(entityTypeName, id)).flatMap(_ =>
        Future.successful(())
      )
    ))
  }

  /** Get the id's of all entity.
   *
   * @return Future containing all id's of the entity type, can fail
   */
  override def readAllIds(): Future[Set[UUID]] = {
    accessAuthorisation.read(ownerId).map(_.listAccess(entityTypeName))
  }

  private def restricted[T](id: UUID, f: => Future[T]): Future[T] = {
    accessAuthorisation.read(ownerId).map(_.checkAccess(entityTypeName, id)).flatMap(accessGranted =>
      if (accessGranted) {
        f
      } else {
        Future.failed(new IllegalStateException("access denied"))
      }
    )
  }

}
