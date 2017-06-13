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
import models.file.File

/**
 * Persistence Implementation for the different types of entitys.
 */
trait Repository {

  /** Persistence for AccessAuthorisation */
  private[persistence] val accessAuthorisations: Persistence[AccessAuthorisation]

  /** Persistence for [[models.entity.EventDrivenTask]] */
  val eventDrivenTasks: Persistence[EventDrivenTask]

  /** Persistence for [[models.entity.BondedTask]] */
  val bondTasks: Persistence[BondedTask]

  /** Persistence for [[models.entity.TimedTask]] */
  val timedTasks: Persistence[TimedTask]

  /** Persistence for [[models.entity.Generator]] */
  val generators: Persistence[Generator]

  /** Persistence for [[models.entity.Filter]] */
  val filters: Persistence[Filter]

  /** Persistence for [[models.entity.GeneratorImage]] */
  val generatorImages: Persistence[GeneratorImage]

  /** Persistence for [[models.entity.FilterImage]] */
  val filterImages: Persistence[FilterImage]

  /** Persistence for [[models.entity.Settings]] */
  val settings: Persistence[Settings]

  /** Persistence for [[models.entity.MetaModelEntity]] */
  val metaModelEntities: Persistence[MetaModelEntity]

  /** Persistence for the metaModelReleases indices */
  private[persistence] val metaModelReleasesIndices: Persistence[VersionIndex]

  /** Persistence for the metaModelReleases versions */
  private[persistence] val metaModelReleasesVersions: Persistence[EntityVersion[MetaModelRelease]]

  /** Persistence for [[models.entity.MetaModelRelease]] */
  final val metaModelReleases = new VersionSystem(metaModelReleasesIndices, metaModelReleasesVersions)(Ordering.Int)

  /** Persistence for [[models.entity.ModelEntity]] */
  val modelEntities: Persistence[ModelEntity]

  /** Persistence for [[models.entity.Log]] */
  val logs: Persistence[Log]

  /** Persistence for [[entity.User]] */
  val users: Persistence[User]

  /** Persistence for the file indices */
  private[persistence] val fileIndices: Persistence[VersionIndex]

  /** Persistence for the file versions */
  private[persistence] val fileVersions: Persistence[EntityVersion[File]]

  /** Versioned Persistence for [[models.file.File]] */
  final val files = new VersionSystem(fileIndices, fileVersions)(Ordering.String)

}
