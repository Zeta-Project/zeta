package de.htwg.zeta.persistence.general

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import models.entity.Entity

/** Interface for the Persistence layer.
 *
 * @tparam E type of the entity
 */
trait EntityPersistence[E <: Entity] { // scalastyle:ignore

  /** The name of the entity-type.
   *
   * @param m manifest
   * @return name
   */
  final def entityTypeName(implicit m: Manifest[E]): String = {
    m.runtimeClass.getSimpleName
  }

  /** Create a new entity.
   *
   * @param entity the entity to save
   * @return Future, with the created entity
   */
  def create(entity: E): Future[E]

  /** Get a single entity.
   *
   * @param id The id of the entity
   * @return Future containing the read entity
   */
  def read(id: UUID): Future[E]

  /** Update a entity.
   *
   * @param id           The id of the entity
   * @param updateEntity Function, to build the updated entity from the existing
   * @return Future containing the updated entity
   */
  final def update(id: UUID, updateEntity: E => E): Future[E] = {
    read(id).flatMap(entity => update(updateEntity(entity)))
  }

  /** Update a entity.
   *
   * @param entity The updated entity
   * @return Future containing the updated entity
   */
  private[persistence] def update(entity: E): Future[E]

  /** Delete a entity.
   *
   * @param id The id of the entity to delete
   * @return Future
   */
  def delete(id: UUID): Future[Unit]

  /** Get the id's of all entity.
   *
   * @return Future containing all id's of the entity type
   */
  def readAllIds(): Future[Set[UUID]]

  /** Read a entity by id. If it doesn't exist create it.
   *
   * @param id     the id of the entity to read
   * @param entity the entity to create, only evaluated when needed (call-by-name)
   * @return The read or created entity
   */
  final def readOrCreate(id: UUID, entity: => E): Future[E] = {
    read(id).recoverWith {
      case _ => create(entity)
    }
  }

  /** Update a entity. If it doesn't exist create it.
   *
   * @param entity the entity to create or update
   * @return The updated or created entity
   */
  final def createOrUpdate(entity: E): Future[E] = {
    update(entity).recoverWith {
      case _ => create(entity)
    }
  }

  /** Update a entity. If it doesn't exist create it.
   *
   * @param id           the id of the entity
   * @param updateEntity function to update the existing entity
   * @param entity       the entity to create
   * @return The updated or created entity
   */
  final def createOrUpdate(id: UUID, updateEntity: E => E, entity: => E): Future[E] = {
    update(id, updateEntity).recoverWith {
      case _ => create(entity)
    }
  }

}
