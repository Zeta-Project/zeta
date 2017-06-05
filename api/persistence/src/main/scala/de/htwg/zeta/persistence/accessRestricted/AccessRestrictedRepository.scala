package de.htwg.zeta.persistence.accessRestricted

import java.util.UUID

import de.htwg.zeta.persistence.general.EntityVersion
import de.htwg.zeta.persistence.general.Persistence
import de.htwg.zeta.persistence.general.Repository
import models.User
import models.document.MetaModelRelease
import models.file.File


/** PersistenceService-Layer to restrict the access to the persistence.
 *
 * @param ownerId     The id of the assigned user to the restriction
 * @param underlaying The underlaying persistence Service
 */
case class AccessRestrictedRepository(ownerId: UUID, underlaying: Repository) extends Repository {

  /** Persistence for AccessAuthorisation */
  override private[persistence] val accessAuthorisations: Persistence[AccessAuthorisation] =
    underlaying.accessAuthorisations

  /** Persistence for the [[models.document.EventDrivenTask]] */
  override lazy val eventDrivenTasks =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlaying.eventDrivenTasks)

  /** Persistence for the [[models.document.BondedTask]] */
  override lazy val bondTasks =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlaying.bondTasks)

  /** Persistence for [[models.document.TimedTask]] */
  override val timedTasks =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlaying.timedTasks)

  /** Persistence for the [[models.document.Generator]] */
  override lazy val generators =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlaying.generators)

  /** Persistence for the [[models.document.Filter]] */
  override lazy val filters =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlaying.filters)

  /** Persistence for the [[models.document.GeneratorImage]] */
  override lazy val generatorImages =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlaying.generatorImages)

  /** Persistence for the [[models.document.FilterImage]] */
  override lazy val filterImages =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlaying.filterImages)

  /** Persistence for the [[models.document.Settings]] */
  override lazy val settings =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlaying.settings)

  /** Persistence for the [[models.document.MetaModelEntity]] */
  override lazy val metaModelEntities =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlaying.metaModelEntities)

  /** Persistence for the metaModelReleases indices */
  override private[persistence] val metaModelReleasesIndices =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlaying.metaModelReleasesIndices)

  /** Persistence for the metaModelReleases versions */
  override private[persistence] val metaModelReleasesVersions: Persistence[EntityVersion[MetaModelRelease]] =
    underlaying.metaModelReleasesVersions

  /** Persistence for the [[models.document.ModelEntity]] */
  override lazy val modelEntities =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlaying.modelEntities)

  /** Persistence for the [[models.document.Log]] */
  override lazy val logs =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlaying.logs)

  /** Persistence for the [[models.User]] */
  override lazy val users: Persistence[User] =
    underlaying.users

  /** Persistence for the file indices */
  override private[persistence] val fileIndices =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlaying.fileIndices)

  /** Persistence for the file versions */
  override private[persistence] val fileVersions: Persistence[EntityVersion[File]] =
    underlaying.fileVersions

}

/** All entity id's a user is authorized to access.
 *
 * @param id               entity-id, same id as [[models.User]]
 * @param authorizedAccess all authorized id's
 */
private[persistence] case class AccessAuthorisation(id: UUID, authorizedAccess: Map[String, Set[UUID]]) {

  /** Add a id to the accessible id's.
   *
   * @param entityType the name of the entity type
   * @param entityId   the entity-id
   * @return copy of this with the added id
   */
  def grantAccess(entityType: String, entityId: UUID): AccessAuthorisation = {
    copy(authorizedAccess = authorizedAccess.updated(
      entityType, authorizedAccess.getOrElse(entityType, Set.empty) + entityId)
    )
  }

  /** Remove a id to the accessible id's.
   *
   * @param entityType the name of the entity type
   * @param entityId   the entity-id
   * @return copy of this with the removed id
   */
  def revokeAccess(entityType: String, entityId: UUID): AccessAuthorisation = {
    val authorizedIds = authorizedAccess.getOrElse(entityType, Set.empty) - entityId
    if (authorizedIds.isEmpty) {
      copy(authorizedAccess = authorizedAccess - entityType)
    } else {
      copy(authorizedAccess = authorizedAccess.updated(entityType, authorizedIds))
    }
  }

  /** List all accessible id's.
   *
   * @param entityType the name of the entity type
   * @return List with the accessible id's
   */
  def listAccess(entityType: String): Set[UUID] = {
    authorizedAccess.getOrElse(entityType, Set.empty)
  }

  /** Check if a id is authorised to access.
   *
   * @param entityType the name of the entity type
   * @param entityId   the entity-id
   * @return List with the accessible id's
   */
  def checkAccess(entityType: String, entityId: UUID): Boolean = {
    listAccess(entityType).contains(entityId)
  }

}
