
package de.htwg.zeta.persistence.accessRestricted

import java.util.UUID

import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.general.Repository
import models.entity
import models.entity.AccessAuthorisation
import models.entity.File
import models.entity.MetaModelRelease
import models.entity.User


/** PersistenceService-Layer to restrict the access to the persistence.
 *
 * @param ownerId    The id of the assigned user to the restriction
 * @param underlying The underlaying persistence Service
 */
case class AccessRestrictedRepository(ownerId: UUID, underlying: Repository) extends Repository {

  /** Persistence for AccessAuthorisation */
  override private[persistence] val accessAuthorisations: EntityPersistence[AccessAuthorisation] =
    underlying.accessAuthorisations

  /** Persistence for the [[models.entity.EventDrivenTask]] */
  override lazy val eventDrivenTasks =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.eventDrivenTasks)

  /** Persistence for the [[models.entity.BondedTask]] */
  override lazy val bondTasks =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.bondTasks)

  /** Persistence for [[models.entity.TimedTask]] */
  override val timedTasks =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.timedTasks)

  /** Persistence for the [[models.entity.Generator]] */
  override lazy val generators =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.generators)

  /** Persistence for the [[models.entity.Filter]] */
  override lazy val filters =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.filters)

  /** Persistence for the [[models.entity.GeneratorImage]] */
  override lazy val generatorImages =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.generatorImages)

  /** Persistence for the [[models.entity.FilterImage]] */
  override lazy val filterImages =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.filterImages)

  /** Persistence for the [[models.entity.Settings]] */
  override lazy val settings =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.settings)

  /** Persistence for the [[models.entity.MetaModelEntity]] */
  override lazy val metaModelEntities =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.metaModelEntities)

  /** Persistence for [[models.entity.MetaModelRelease]] */
  override val metaModelReleases: EntityPersistence[MetaModelRelease] =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.metaModelReleases)

  /** Persistence for the [[models.entity.ModelEntity]] */
  override lazy val modelEntities =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.modelEntities)

  /** Persistence for the [[models.entity.Log]] */
  override lazy val logs =
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.logs)

  /** Persistence for the [[entity.User]] */
  override lazy val users: EntityPersistence[User] =
    underlying.users

  /** Versioned Persistence for [[File]] */
  override val files: FilePersistence = null // TODO

}
