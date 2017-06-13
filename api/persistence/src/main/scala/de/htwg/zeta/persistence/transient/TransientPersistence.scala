package de.htwg.zeta.persistence.transient

import java.util.UUID

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

import de.htwg.zeta.persistence.general.EntityPersistence
import models.entity.Entity

/** Cache implementation of [[EntityPersistence]].
 *
 * @tparam E type of the entity
 */
class TransientPersistence[E <: Entity] extends EntityPersistence[E] { // scalastyle:ignore

  private val cache: TrieMap[UUID, E] = TrieMap.empty[UUID, E]

  /** Create a new entity.
   *
   * @param entity the entity to save
   * @return Future, with the created entity
   */
  override def create(entity: E): Future[E] = {
    if (cache.putIfAbsent(entity.id, entity).isEmpty) {
      Future.successful(entity)
    } else {
      Future.failed(new IllegalArgumentException("cant't create the entity, a entity with same id already exists"))
    }
  }

  /** Get a single entity.
   *
   * @param id The id of the entity
   * @return Future which resolve with the entity and can fail
   */
  override def read(id: UUID): Future[E] = {
    cache.get(id).fold[Future[E]] {
      Future.failed(new IllegalArgumentException("can't read the entity, a entity with the id doesn't exist"))
    } {
      Future.successful
    }
  }

  /** Update a entity.
   *
   * @param entity The updated entity
   * @return Future containing the updated entity
   */
  override private[persistence] def update(entity: E): Future[E] = {
    if (cache.replace(entity.id, entity).isDefined) {
      Future.successful(entity)
    } else {
      Future.failed(new IllegalArgumentException("can't update the document, a document with the id doesn't exist"))
    }
  }

  /** Delete a entity.
   *
   * @param id The id of the entity to delete
   * @return Future, which can fail
   */
  override def delete(id: UUID): Future[Unit] = {
    if (cache.remove(id).isDefined) {
      Future.successful(Unit)
    } else {
      Future.failed(new IllegalArgumentException("can't delete the entity, a entity with the id doesn't exist"))
    }
  }

  /** Get the id's of all entitys.
   *
   * @return Future containing all id's of the entity type, can fail
   */
  override def readAllIds(): Future[Set[UUID]] = {
    Future.successful(cache.keys.toSet)
  }


}
