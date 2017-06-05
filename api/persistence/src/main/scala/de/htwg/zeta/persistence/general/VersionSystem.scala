package de.htwg.zeta.persistence.general

import java.util.UUID

import scala.collection.immutable.SortedMap
import scala.concurrent.Future

import com.softwaremill.quicklens.ModifyPimp
import models.Entity

/** Persistence to hold multiple version of a Entity with the same id.
 *
 * @tparam K The unique version key
 * @tparam E The Entity-Type
 */
class VersionSystem[K, E <: Entity]( // scalastyle:ignore
    versionPersistence: Persistence[VersionIndex[K]],
    entityPersistence: Persistence[EntityVersion[E]]) {

  /** Read the latest version of a entity.
   *
   * @param id the entity-id
   * @return latest version
   */
  def readLatestVersion(id: UUID): Future[E] = {
    versionPersistence.read(id).map(_.versions.last._2).flatMap(versionId =>
      entityPersistence.read(versionId)
    ).map(_.entity)
  }

  /** Read all version keys, the keys are sorted from the oldest to the newest.
   *
   * @param id the entity-id
   * @return sorted version keys
   */
  def readVersionKeys(id: UUID): Future[Seq[K]] = {
    versionPersistence.read(id).map(_.versions.keySet.toSeq)
  }

  /** Add a new version.
   *
   * @param version The new versioned UUID.
   * @param entity  The Entity
   * @return A copy containing the new version.
   */
  def createVersion(version: K, entity: E): Future[E] = {
    entityPersistence.create(EntityVersion(UUID.randomUUID, entity)).flatMap(versionedEntity =>
      versionPersistence.update(
        entity.id, _.modify(_.versions).using(_ + (version -> versionedEntity.id))
      ).recoverWith {
        case _ => versionPersistence.create(
          VersionIndex(entity.id, SortedMap(version -> versionedEntity.id))
        )
      }.map(_ => entity)
    )
  }

  /** Read a version of a entity.
   *
   * @param id      the entity-id
   * @param version the version-key
   * @return the entity
   */
  def readVersion(id: UUID, version: K): Future[E] = {
    versionPersistence.read(id).map(_.versions(version)).flatMap(versionId =>
      entityPersistence.read(versionId)
    ).map(_.entity)
  }

  /** Read a version of a entity.
   *
   * @param id           the entity-id
   * @param updateEntity function to update the entity
   * @param version      the version-key
   * @return the entity
   */
  def updateVersion(id: UUID, updateEntity: E => E, version: K): Future[E] = {
    versionPersistence.read(id).map(_.versions(version)).flatMap(versionId =>
      entityPersistence.update(versionId, _.modify(_.entity).using(updateEntity))
    ).map(_.entity)
  }

  /** Delete a version of a entity. If this is the only version, the whole VersionSystem is deleted.
   *
   * @param id      the entity-id
   * @param version the version-key
   * @return the entity
   */
  def deleteVersion(id: UUID, version: K): Future[Unit] = {
    versionPersistence.read(id).map(_.versions(version)).flatMap(versionId =>
      entityPersistence.delete(versionId)
    ).flatMap(_ =>
      versionPersistence.read(id).flatMap(x =>
        if (x.versions.isEmpty) {
          versionPersistence.delete(id)
        } else {
          Future.successful(())
        }
      )
    )
  }

}

/** Holds all the all version Keys of a entity.
 *
 * @param id       the entity-id
 * @param versions the version-keys
 * @tparam K the key-type
 */
private[persistence] case class VersionIndex[K](id: UUID, versions: SortedMap[K, UUID]) extends Entity

/** Holds a single version of a entity.
 *
 * @param id     the version id
 * @param entity the entity
 * @tparam E the entity-type
 */
private[persistence] case class EntityVersion[E <: Entity](id: UUID, entity: E) extends Entity // scalastyle:ignore
