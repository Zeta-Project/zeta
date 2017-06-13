package de.htwg.zeta.persistence.transient

import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.general.Repository
import models.entity
import models.entity.AccessAuthorisation
import models.entity.BondedTask
import models.entity.EventDrivenTask
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


/** Cache-Implementation of the PersistenceService. */
class TransientRepository extends Repository {

  /** Persistence for AccessAuthorisation */
  override private[persistence] val accessAuthorisations = new TransientPersistence[AccessAuthorisation]

  /** Persistence for the [[models.entity.EventDrivenTask]] */
  override val eventDrivenTasks = new TransientPersistence[EventDrivenTask]

  /** Persistence for the [[models.entity.BondedTask]] */
  override val bondTasks = new TransientPersistence[BondedTask]

  /** Persistence for [[models.entity.TimedTask]] */
  override val timedTasks = new TransientPersistence[TimedTask]

  /** Persistence for the [[models.entity.Generator]] */
  override val generators = new TransientPersistence[Generator]

  /** Persistence for the [[models.entity.Filter]] */
  override val filters = new TransientPersistence[Filter]

  /** Persistence for the [[models.entity.GeneratorImage]] */
  override val generatorImages = new TransientPersistence[GeneratorImage]

  /** Persistence for the [[models.entity.FilterImage]] */
  override val filterImages = new TransientPersistence[FilterImage]

  /** Persistence for the [[models.entity.Settings]] */
  override val settings = new TransientPersistence[Settings]

  /** Persistence for the [[models.entity.MetaModelEntity]] */
  override val metaModelEntities = new TransientPersistence[MetaModelEntity]



  /** Persistence for the [[models.entity.ModelEntity]] */
  override val modelEntities = new TransientPersistence[ModelEntity]

  /** Persistence for the [[models.entity.Log]] */
  override val logs = new TransientPersistence[Log]

  /** Persistence for the [[User]] */
  override val users = new TransientPersistence[User]

  /** Persistence for [[models.entity.MetaModelRelease]] */
  override val metaModelReleases = new TransientPersistence[MetaModelRelease]

  /** Versioned Persistence for [[entity.File]] */
  override val files: FilePersistence = null // TODO
}
