package de.htwg.zeta.persistence.general

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

/**
 * Persistence Implementation for the different types of documents.
 */
trait Repository {

  /** Persistence for [[models.document.EventDrivenTask]] */
  val eventDrivenTasks: Persistence[EventDrivenTask]

  /** Persistence for [[models.document.BondedTask]] */
  val bondTasks: Persistence[BondedTask]

  /** Persistence for [[models.document.TimedTask]] */
  val timedTasks: Persistence[TimedTask]

  /** Persistence for [[models.document.Generator]] */
  val generators: Persistence[Generator]

  /** Persistence for [[models.document.Filter]] */
  val filters: Persistence[Filter]

  /** Persistence for [[models.document.GeneratorImage]] */
  val generatorImages: Persistence[GeneratorImage]

  /** Persistence for [[models.document.FilterImage]] */
  val filterImages: Persistence[FilterImage]

  /** Persistence for [[models.document.Settings]] */
  val settings: Persistence[Settings]

  /** Persistence for [[models.document.MetaModelEntity]] */
  val metaModelEntities: Persistence[MetaModelEntity]

  /** Persistence for [[models.document.MetaModelRelease]] */
  val metaModelReleases: Persistence[MetaModelRelease]

  /** Persistence for [[models.document.ModelEntity]] */
  val modelEntities: Persistence[ModelEntity]

  /** Persistence for [[models.document.Log]] */
  val logs: Persistence[Log]

  /** Persistence for [[models.User]] */
  val users: Persistence[User]


  /** Persistence for the file indices */
  private[persistence] val fileIndices: Persistence[VersionIndex[String]]

  /** Persistence for the file versions */
  private[persistence] val fileVersions: Persistence[EntityVersion[File]]

  /** Versioned Persistence for [[models.file.File]] */
  final val files = new VersionSystem(fileIndices, fileVersions)



}
