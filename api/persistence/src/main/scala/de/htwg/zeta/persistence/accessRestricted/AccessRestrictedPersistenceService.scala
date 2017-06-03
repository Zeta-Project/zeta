package de.htwg.zeta.persistence.accessRestricted

import de.htwg.zeta.persistence.general.PersistenceService
import models.User


/** PersistenceService-Layer to restrict the access to the persistence.
 *
 * @param owner       The assigned user to the restriction
 * @param underlaying The underlaying persistence Service
 */
case class AccessRestrictedPersistenceService(owner: User, underlaying: PersistenceService) extends PersistenceService {

  /** Persistence for the [[models.document.EventDrivenTask]] */
  override lazy val eventDrivenTask = AccessRestrictedPersistence(owner.accessAuthorisation.eventDrivenTask, underlaying.eventDrivenTask)

  /** Persistence for the [[models.document.BondedTask]] */
  override lazy val bondTask = AccessRestrictedPersistence(owner.accessAuthorisation.bondTask, underlaying.bondTask)

  /** Persistence for the [[models.document.Generator]] */
  override lazy val generator = AccessRestrictedPersistence(owner.accessAuthorisation.generator, underlaying.generator)

  /** Persistence for the [[models.document.Filter]] */
  override lazy val filter = AccessRestrictedPersistence(owner.accessAuthorisation.filter, underlaying.filter)

  /** Persistence for the [[models.document.GeneratorImage]] */
  override lazy val generatorImage = AccessRestrictedPersistence(owner.accessAuthorisation.generatorImage, underlaying.generatorImage)

  /** Persistence for the [[models.document.FilterImage]] */
  override lazy val filterImage = AccessRestrictedPersistence(owner.accessAuthorisation.filterImage, underlaying.filterImage)

  /** Persistence for the [[models.document.Settings]] */
  override lazy val settings = AccessRestrictedPersistence(owner.accessAuthorisation.settings, underlaying.settings)

  /** Persistence for the [[models.document.MetaModelEntity]] */
  override lazy val metaModelEntity = AccessRestrictedPersistence(owner.accessAuthorisation.metaModelEntity, underlaying.metaModelEntity)

  /** Persistence for the [[models.document.MetaModelRelease]] */
  override lazy val metaModelRelease = AccessRestrictedPersistence(owner.accessAuthorisation.metaModelRelease, underlaying.metaModelRelease)

  /** Persistence for the [[models.document.ModelEntity]] */
  override lazy val modelEntity = AccessRestrictedPersistence(owner.accessAuthorisation.modelEntity, underlaying.modelEntity)

  /** Persistence for the [[models.document.Log]] */
  override lazy val log = AccessRestrictedPersistence(owner.accessAuthorisation.log, underlaying.log)

  /** Persistence for the [[models.document.UserEntity]] */
  override lazy val users = AccessRestrictedPersistence(Seq(owner.id), underlaying.users)

}
