package de.htwg.zeta.persistence.transient

import de.htwg.zeta.persistence.accessRestricted.AccessAuthorisation
import de.htwg.zeta.persistence.general.EntityVersion
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.general.VersionIndex
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
import models.file.File


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

  /** Persistence for the metaModelReleases indices */
  override private[persistence] val metaModelReleasesIndices = new TransientPersistence[VersionIndex]

  /** Persistence for the metaModelReleases versions */
  override private[persistence] val metaModelReleasesVersions = new TransientPersistence[EntityVersion[MetaModelRelease]]

  /** Persistence for the [[models.entity.ModelEntity]] */
  override val modelEntities = new TransientPersistence[ModelEntity]

  /** Persistence for the [[models.entity.Log]] */
  override val logs = new TransientPersistence[Log]

  /** Persistence for the [[User]] */
  override val users = new TransientPersistence[User]

  /** Persistence for the file indices */
  override private[persistence] val fileIndices = new TransientPersistence[VersionIndex]

  /** Persistence for the file versions */
  override private[persistence] val fileVersions = new TransientPersistence[EntityVersion[File]]

}
