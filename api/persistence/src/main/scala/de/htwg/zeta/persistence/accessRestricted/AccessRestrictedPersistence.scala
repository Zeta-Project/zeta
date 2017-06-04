package de.htwg.zeta.persistence.accessRestricted

import java.util.UUID

import scala.concurrent.Future

import de.htwg.zeta.persistence.general.Persistence
import models.Identifiable


/** Persistence-Layer to restrict the access to the persistence.
 *
 * @param access      Seq with all access-authorized id's
 * @param underlaying The underlaying Persistence
 * @tparam E type of the entity
 */
case class AccessRestrictedPersistence[E <: Identifiable](access: AccessHelper, underlaying: Persistence[E]) extends Persistence[E] { // scalastyle:ignore

  /** Create a new entity.
   *
   * @param entity the entity to save
   * @return Future, which can fail
   */
  override def create(entity: E): Future[E] = {
    underlaying.create(entity).flatMap(entity => {
      access.grantAccess(entity.id).flatMap { _ =>
        Future.successful(entity)
      }
    })
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
      access.revokeAccess(id).flatMap(_ =>
        Future.successful(())
      )
    ))
  }

  /** Get the id's of all entity.
   *
   * @return Future containing all id's of the entity type, can fail
   */
  override def readAllIds(): Future[Set[UUID]] = {
    access.listAccess
  }

  private def restricted[T](id: UUID, f: => Future[T]): Future[T] = {
    access.checkAccess(id).flatMap(_ => f)
  }

}
