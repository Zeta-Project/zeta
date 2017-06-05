package de.htwg.zeta.persistence.general

import java.util.UUID

import scala.concurrent.Future

import models.Entity

/** Interface for the Persistence layer.
 *
 * @tparam E type of the entity
 */
trait Persistence[E <: Entity] { // scalastyle:ignore

  /** The name of the entity-type.
   *
   * @param m manifest
   * @return name
   */
  final def name(implicit m: Manifest[E]): String = {
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
   * @param id The id of the entity
   * @param updateEntity Function, to build the updated entity from the existing
   * @return Future containing the updated entity
   */
  def update(id: UUID, updateEntity: E => E): Future[E]


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

}
