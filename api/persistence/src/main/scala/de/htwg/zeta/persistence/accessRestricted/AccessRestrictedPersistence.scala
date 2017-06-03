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
case class AccessRestrictedPersistence[E <: Identifiable](access: Seq[UUID], underlaying: Persistence[E]) extends Persistence[E] { // scalastyle:ignore

  /** Create a new entity.
   *
   * @param entity the entity to save
   * @return Future, which can fail
   */
  override def create(entity: E): Future[Unit] = {
    underlaying.create(entity)
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
   * @param entity The entity to update
   * @return Future, which can fail
   */
  override def update(entity: E): Future[Unit] = {
    restricted(entity.id, underlaying.update(entity))
  }

  /** Delete a entity.
   *
   * @param id The id of the entity to delete
   * @return Future, which can fail
   */
  override def delete(id: UUID): Future[Unit] = {
    restricted(id, underlaying.delete(id))
  }

  /** Get the id's of all entity.
   *
   * @return Future containing all id's of the entity type, can fail
   */
  override def readAllIds: Future[Seq[UUID]] = {
    Future.successful(access)
  }

  private def restricted[T](id: UUID, f: => Future[T]): Future[T] = {
    if (access.contains(id)) {
      f
    } else {
      Future.failed(new IllegalStateException("Entity doesn't exists or the access is denied"))
    }
  }

}
