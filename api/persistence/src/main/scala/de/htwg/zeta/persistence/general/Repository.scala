package de.htwg.zeta.persistence.general

import de.htwg.zeta.persistence.accessRestricted.AccessAuthorisation
import models.entity
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

/** Persistence Implementation for the different types of entities. */
trait Repository {

  /** Persistence for AccessAuthorisation */
  private[persistence] val accessAuthorisations: EntityPersistence[AccessAuthorisation]

  /** Persistence for [[models.entity.EventDrivenTask]] */
  val eventDrivenTasks: EntityPersistence[EventDrivenTask]

  /** Persistence for [[models.entity.BondedTask]] */
  val bondTasks: EntityPersistence[BondedTask]

  /** Persistence for [[models.entity.TimedTask]] */
  val timedTasks: EntityPersistence[TimedTask]

  /** Persistence for [[models.entity.Generator]] */
  val generators: EntityPersistence[Generator]

  /** Persistence for [[models.entity.Filter]] */
  val filters: EntityPersistence[Filter]

  /** Persistence for [[models.entity.GeneratorImage]] */
  val generatorImages: EntityPersistence[GeneratorImage]

  /** Persistence for [[models.entity.FilterImage]] */
  val filterImages: EntityPersistence[FilterImage]

  /** Persistence for [[models.entity.Settings]] */
  val settings: EntityPersistence[Settings]

  /** Persistence for [[models.entity.MetaModelEntity]] */
  val metaModelEntities: EntityPersistence[MetaModelEntity]

  /** Persistence for [[models.entity.MetaModelRelease]] */
  val metaModelReleases: EntityPersistence[MetaModelRelease]

  /** Persistence for [[models.entity.ModelEntity]] */
  val modelEntities: EntityPersistence[ModelEntity]

  /** Persistence for [[models.entity.Log]] */
  val logs: EntityPersistence[Log]

  /** Persistence for [[entity.User]] */
  val users: EntityPersistence[User]

  /** Versioned Persistence for [[models.file.File]] */
  val files: FilePersistence

}
