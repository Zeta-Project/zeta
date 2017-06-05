package de.htwg.zeta.persistence.transient

import de.htwg.zeta.persistence.general.EntityVersion
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.general.VersionIndex
import models.User
import models.document.BondedTask
import models.document.EventDrivenTask
import models.document.Filter
import models.document.FilterImage
import models.document.Generator
import models.document.GeneratorImage
import models.document.Log
import models.document.MetaModelEntity
import models.document.MetaModelRelease
import models.document.ModelEntity
import models.document.Settings
import models.document.TimedTask
import models.file.File


/** Cache-Implementation of the PersistenceService. */
class TransientRepository extends Repository {

  /** Persistence for the [[models.document.EventDrivenTask]] */
  override val eventDrivenTasks = new TransientPersistence[EventDrivenTask]

  /** Persistence for the [[models.document.BondedTask]] */
  override val bondTasks = new TransientPersistence[BondedTask]

  /** Persistence for [[models.document.TimedTask]] */
  override val timedTasks = new TransientPersistence[TimedTask]

  /** Persistence for the [[models.document.Generator]] */
  override val generators = new TransientPersistence[Generator]

  /** Persistence for the [[models.document.Filter]] */
  override val filters = new TransientPersistence[Filter]

  /** Persistence for the [[models.document.GeneratorImage]] */
  override val generatorImages = new TransientPersistence[GeneratorImage]

  /** Persistence for the [[models.document.FilterImage]] */
  override val filterImages = new TransientPersistence[FilterImage]

  /** Persistence for the [[models.document.Settings]] */
  override val settings = new TransientPersistence[Settings]

  /** Persistence for the [[models.document.MetaModelEntity]] */
  override val metaModelEntities = new TransientPersistence[MetaModelEntity]

  /** Persistence for the metaModelReleases indices */
  override private[persistence] val metaModelReleasesIndices = new TransientPersistence[VersionIndex[Int]]

  /** Persistence for the metaModelReleases versions */
  override private[persistence] val metaModelReleasesVersions = new TransientPersistence[EntityVersion[MetaModelRelease]]

  /** Persistence for the [[models.document.ModelEntity]] */
  override val modelEntities = new TransientPersistence[ModelEntity]

  /** Persistence for the [[models.document.Log]] */
  override val logs = new TransientPersistence[Log]

  /** Persistence for the [[models.User]] */
  override val users = new TransientPersistence[User]

  /** Persistence for the file indices */
  override private[persistence] val fileIndices = new TransientPersistence[VersionIndex[String]]

  /** Persistence for the file versions */
  override private[persistence] val fileVersions = new TransientPersistence[EntityVersion[File]]

}
