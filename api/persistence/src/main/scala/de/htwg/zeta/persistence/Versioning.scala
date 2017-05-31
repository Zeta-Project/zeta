package de.htwg.zeta.persistence

import java.util.UUID

import scala.collection.immutable.SortedMap

import akka.http.scaladsl.model.DateTime
import models.Identifiable


/** A versioned Identifiable.
 *
 * @param id       The id of the IdVersioning it self
 * @param versions A map with all DateTime's and UUID's of the versioned Entities.
 */
case class Versioning(id: UUID = UUID.randomUUID, versions: SortedMap[DateTime, UUID]) extends Identifiable {

  /** Constructor to create a IdVersioning with only the first element.
   *
   * @param version  The id of the IdVersioning it self
   * @param dateTime The DateTime of the versioned UUID.
   * @param id       The versioned UUID.
   */
  def this(version: UUID, dateTime: DateTime = DateTime.now, id: UUID = UUID.randomUUID) = {
    this(id, SortedMap(dateTime -> UUID))
  }

  /** Get the current version.
   *
   * @return UUID
   */
  def current: UUID = {
    versions.head._2
  }

  /** Add a new version.
   *
   * @param version The new versioned UUID.
   * @param dateTime The DateTime of the ner versioned UUID.
   * @return A copy containing the new version.
   */
  def addVersion(version: UUID, dateTime: DateTime = DateTime.now): Versioning = copy(versions = versions + (dateTime -> version))

}
