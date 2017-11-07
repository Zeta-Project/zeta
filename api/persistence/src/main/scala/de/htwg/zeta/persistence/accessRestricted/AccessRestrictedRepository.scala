package de.htwg.zeta.persistence.accessRestricted

import java.util.UUID

import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.EventDrivenTask
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.FilterImage
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.Log
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.entity.Settings
import de.htwg.zeta.common.models.entity.TimedTask
import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.PasswordInfoPersistence
import de.htwg.zeta.persistence.general.Repository


/** PersistenceService-Layer to restrict the access to the persistence.
 *
 * @param ownerId    The id of the assigned user to the restriction
 * @param underlying The underlying persistence Service
 */
class AccessRestrictedRepository(ownerId: UUID, underlying: Repository) extends Repository {

  /** Persistence for AccessAuthorisation */
  override def accessAuthorisation: EntityPersistence[AccessAuthorisation] = {
    throw new UnsupportedOperationException("access to AccessAuthorisation-Persistence can't be restricted")
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.EventDrivenTask]] */
  override def eventDrivenTask: EntityPersistence[EventDrivenTask] = {
    new AccessRestrictedEntityPersistence(underlying.accessAuthorisation, underlying.eventDrivenTask).restrictedTo(ownerId)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.BondedTask]] */
  override def bondedTask: EntityPersistence[BondedTask] = {
    new AccessRestrictedEntityPersistence(underlying.accessAuthorisation, underlying.bondedTask).restrictedTo(ownerId)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.TimedTask]] */
  override def timedTask: EntityPersistence[TimedTask] = {
    new AccessRestrictedEntityPersistence(underlying.accessAuthorisation, underlying.timedTask).restrictedTo(ownerId)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Generator]] */
  override def generator: EntityPersistence[Generator] = {
    new AccessRestrictedEntityPersistence(underlying.accessAuthorisation, underlying.generator).restrictedTo(ownerId)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Filter]] */
  override def filter: EntityPersistence[Filter] = {
    new AccessRestrictedEntityPersistence(underlying.accessAuthorisation, underlying.filter).restrictedTo(ownerId)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.GeneratorImage]] */
  override def generatorImage: EntityPersistence[GeneratorImage] = {
    new AccessRestrictedEntityPersistence(underlying.accessAuthorisation, underlying.generatorImage).restrictedTo(ownerId)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.FilterImage]] */
  override def filterImage: EntityPersistence[FilterImage] = {
    new AccessRestrictedEntityPersistence(underlying.accessAuthorisation, underlying.filterImage).restrictedTo(ownerId)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Settings]] */
  override def settings: EntityPersistence[Settings] = {
    new AccessRestrictedEntityPersistence(underlying.accessAuthorisation, underlying.settings).restrictedTo(ownerId)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.MetaModelEntity]] */
  override def metaModelEntity: EntityPersistence[MetaModelEntity] = {
    new AccessRestrictedEntityPersistence(underlying.accessAuthorisation, underlying.metaModelEntity).restrictedTo(ownerId)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelRelease]] */
  override def metaModelRelease: EntityPersistence[MetaModelRelease] = {
    new AccessRestrictedEntityPersistence(underlying.accessAuthorisation, underlying.metaModelRelease).restrictedTo(ownerId)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.ModelEntity]] */
  override def modelEntity: EntityPersistence[ModelEntity] = {
    new AccessRestrictedEntityPersistence(underlying.accessAuthorisation, underlying.modelEntity).restrictedTo(ownerId)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Log]] */
  override def log: EntityPersistence[Log] = {
    new AccessRestrictedEntityPersistence(underlying.accessAuthorisation, underlying.log).restrictedTo(ownerId)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.User]] */
  override def user: EntityPersistence[User] = {
    throw new UnsupportedOperationException("access to User-Persistence can't be restricted")
  }

  /** Versioned Persistence for [[de.htwg.zeta.common.models.entity.File]] */
  override def file: FilePersistence = {
    new AccessRestrictedFilePersistence(underlying.accessAuthorisation, underlying.file).restrictedTo(ownerId)
  }

  /** Persistence for [[com.mohiva.play.silhouette.api.LoginInfo]] */
  override def loginInfo: LoginInfoPersistence = {
    throw new UnsupportedOperationException("access to LoginInfo-Persistence can't be restricted")
  }

  /** Persistence for [[com.mohiva.play.silhouette.api.util.PasswordInfo]] */
  override def passwordInfo: PasswordInfoPersistence = {
    throw new UnsupportedOperationException("access to PasswordInfo-Persistence can't be restricted")
  }

}
