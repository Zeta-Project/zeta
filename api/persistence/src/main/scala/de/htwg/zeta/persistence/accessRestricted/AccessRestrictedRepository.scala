
package de.htwg.zeta.persistence.accessRestricted

import java.util.UUID

import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.general.Repository
import models.entity
import models.entity.AccessAuthorisation
import models.entity.BondedTask
import models.entity.EventDrivenTask
import models.entity.File
import models.entity.Filter
import models.entity.FilterImage
import models.entity.Generator
import models.entity.GeneratorImage
import models.entity.Log
import models.entity.MetaModelEntity
import models.entity.MetaModelRelease
import models.entity.ModelEntity
import models.entity.Settings
import models.entity.TimedTask
import models.entity.User


/** PersistenceService-Layer to restrict the access to the persistence.
 *
 * @param ownerId    The id of the assigned user to the restriction
 * @param underlying The underlaying persistence Service
 */
case class AccessRestrictedRepository(ownerId: UUID, underlying: Repository) extends Repository {

  /** Persistence for AccessAuthorisation */
  override def accessAuthorisations: EntityPersistence[AccessAuthorisation] = {
    underlying.accessAuthorisations
  }

  /** Persistence for the [[models.entity.EventDrivenTask]] */
  override def eventDrivenTasks: AccessRestrictedPersistence[EventDrivenTask] = {
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.eventDrivenTasks)
  }

  /** Persistence for the [[models.entity.BondedTask]] */
  override def bondTasks: AccessRestrictedPersistence[BondedTask] = {
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.bondTasks)
  }

  /** Persistence for [[models.entity.TimedTask]] */
  override def timedTasks: AccessRestrictedPersistence[TimedTask] = {
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.timedTasks)
  }

  /** Persistence for the [[models.entity.Generator]] */
  override def generators: AccessRestrictedPersistence[Generator] = {
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.generators)
  }

  /** Persistence for the [[models.entity.Filter]] */
  override def filters: AccessRestrictedPersistence[Filter] = {
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.filters)
  }

  /** Persistence for the [[models.entity.GeneratorImage]] */
  override def generatorImages: AccessRestrictedPersistence[GeneratorImage] = {
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.generatorImages)
  }

  /** Persistence for the [[models.entity.FilterImage]] */
  override def filterImages: AccessRestrictedPersistence[FilterImage] = {
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.filterImages)
  }

  /** Persistence for the [[models.entity.Settings]] */
  override def settings: AccessRestrictedPersistence[Settings] = {
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.settings)
  }

  /** Persistence for the [[models.entity.MetaModelEntity]] */
  override def metaModelEntities: AccessRestrictedPersistence[MetaModelEntity] = {
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.metaModelEntities)
  }

  /** Persistence for [[models.entity.MetaModelRelease]] */
  override def metaModelReleases: EntityPersistence[MetaModelRelease] = {
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.metaModelReleases)
  }

  /** Persistence for the [[models.entity.ModelEntity]] */
  override def modelEntities: AccessRestrictedPersistence[ModelEntity] = {
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.modelEntities)
  }

  /** Persistence for the [[models.entity.Log]] */
  override def logs: AccessRestrictedPersistence[Log] = {
    AccessRestrictedPersistence(ownerId, accessAuthorisations, underlying.logs)
  }

  /** Persistence for the [[entity.User]] */
  override def users: EntityPersistence[User] = {
    underlying.users
  }

  /** Versioned Persistence for [[File]] */
  override def files: FilePersistence = {
    null // TODO
  }

}
