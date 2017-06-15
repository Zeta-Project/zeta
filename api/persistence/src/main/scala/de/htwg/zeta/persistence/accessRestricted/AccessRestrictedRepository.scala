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
 * @param underlying The underlaying persistence Service
 */
case class AccessRestrictedRepository(ownerId: UUID, underlying: Repository) extends Repository {

  /** Persistence for AccessAuthorisation */
  override def accessAuthorisation: EntityPersistence[AccessAuthorisation] = {
    throw new UnsupportedOperationException("access to AccessAuthorisation-Persistence can't be restricted")
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.EventDrivenTask]] */
  override def eventDrivenTask: AccessRestrictedPersistence[EventDrivenTask] = {
    AccessRestrictedPersistence(ownerId, underlying.accessAuthorisation, underlying.eventDrivenTask)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.BondedTask]] */
  override def bondedTask: AccessRestrictedPersistence[BondedTask] = {
    AccessRestrictedPersistence(ownerId, underlying.accessAuthorisation, underlying.bondedTask)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.TimedTask]] */
  override def timedTask: AccessRestrictedPersistence[TimedTask] = {
    AccessRestrictedPersistence(ownerId, underlying.accessAuthorisation, underlying.timedTask)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Generator]] */
  override def generator: AccessRestrictedPersistence[Generator] = {
    AccessRestrictedPersistence(ownerId, underlying.accessAuthorisation, underlying.generator)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Filter]] */
  override def filter: AccessRestrictedPersistence[Filter] = {
    AccessRestrictedPersistence(ownerId, underlying.accessAuthorisation, underlying.filter)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.GeneratorImage]] */
  override def generatorImage: AccessRestrictedPersistence[GeneratorImage] = {
    AccessRestrictedPersistence(ownerId, underlying.accessAuthorisation, underlying.generatorImage)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.FilterImage]] */
  override def filterImage: AccessRestrictedPersistence[FilterImage] = {
    AccessRestrictedPersistence(ownerId, underlying.accessAuthorisation, underlying.filterImage)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Settings]] */
  override def settings: AccessRestrictedPersistence[Settings] = {
    AccessRestrictedPersistence(ownerId, underlying.accessAuthorisation, underlying.settings)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.MetaModelEntity]] */
  override def metaModelEntity: AccessRestrictedPersistence[MetaModelEntity] = {
    AccessRestrictedPersistence(ownerId, underlying.accessAuthorisation, underlying.metaModelEntity)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelRelease]] */
  override def metaModelRelease: EntityPersistence[MetaModelRelease] = {
    AccessRestrictedPersistence(ownerId, underlying.accessAuthorisation, underlying.metaModelRelease)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.ModelEntity]] */
  override def modelEntity: AccessRestrictedPersistence[ModelEntity] = {
    AccessRestrictedPersistence(ownerId, underlying.accessAuthorisation, underlying.modelEntity)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Log]] */
  override def log: AccessRestrictedPersistence[Log] = {
    AccessRestrictedPersistence(ownerId, underlying.accessAuthorisation, underlying.log)
  }

  /** Persistence for the [[de.htwg.zeta.common.models.entity.User]] */
  override def user: EntityPersistence[User] = {
    throw new UnsupportedOperationException("access to User-Persistence can't be restricted")
  }

  /** Versioned Persistence for [[de.htwg.zeta.common.models.entity.File]] */
  override def file: FilePersistence = {
    null // TODO implement own AccessRestrictedPersistence for files
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
