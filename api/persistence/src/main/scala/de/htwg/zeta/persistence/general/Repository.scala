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

/**
 * Persistence Implementation for the different types of documents.
 */
trait Repository {

  /** Persistence for [[models.document.EventDrivenTask]] */
  val eventDrivenTask: Persistence[EventDrivenTask]

  /** Persistence for [[models.document.BondedTask]] */
  val bondTask: Persistence[BondedTask]

  /** Persistence for [[models.document.Generator]] */
  val generator: Persistence[Generator]

  /** Persistence for [[models.document.Filter]] */
  val filter: Persistence[Filter]

  /** Persistence for [[models.document.GeneratorImage]] */
  val generatorImage: Persistence[GeneratorImage]

  /** Persistence for [[models.document.FilterImage]] */
  val filterImage: Persistence[FilterImage]

  /** Persistence for [[models.document.Settings]] */
  val settings: Persistence[Settings]

  /** Persistence for [[models.document.MetaModelEntity]] */
  val metaModelEntity: Persistence[MetaModelEntity]

  /** Persistence for [[models.document.MetaModelRelease]] */
  val metaModelRelease: Persistence[MetaModelRelease]

  /** Persistence for [[models.document.ModelEntity]] */
  val modelEntity: Persistence[ModelEntity]

  /** Persistence for [[models.document.Log]] */
  val log: Persistence[Log]

  /** Persistence for [[models.User]] */
  val users: Persistence[User]

}
