package de.htwg.zeta.persistence.microService

import de.htwg.zeta.persistence.dbaccess.Persistence
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
import models.document.PasswordInfoEntity
import models.document.Settings
import models.document.UserEntity

/**
 * Persistence Implementation for the different types of documents.
 */
trait PersistenceService {

  /** Persistence for the [[models.document.EventDrivenTask]] */
  val eventDrivenTask: Persistence[EventDrivenTask]

  /** Persistence for the [[models.document.BondedTask]] */
  val bondTask: Persistence[BondedTask]

  /** Persistence for the [[models.document.Generator]] */
  val generator: Persistence[Generator]

  /** Persistence for the [[models.document.Filter]] */
  val filter: Persistence[Filter]

  /** Persistence for the [[models.document.GeneratorImage]] */
  val generatorImage: Persistence[GeneratorImage]

  /** Persistence for the [[models.document.FilterImage]] */
  val filterImage: Persistence[FilterImage]

  /** Persistence for the [[models.document.Settings]] */
  val settings: Persistence[Settings]

  /** Persistence for the [[models.document.MetaModelEntity]] */
  val metaModelEntity: Persistence[MetaModelEntity]

  /** Persistence for the [[models.document.MetaModelRelease]] */
  val metaModelRelease: Persistence[MetaModelRelease]

  /** Persistence for the [[models.document.ModelEntity]] */
  val modelEntity: Persistence[ModelEntity]

  /** Persistence for the [[models.document.Log]] */
  val log: Persistence[Log]

  /** Persistence for the [[models.document.PasswordInfoEntity]] */
  val passwordInfoEntity: Persistence[PasswordInfoEntity]

  /** Persistence for the [[models.document.UserEntity]] */
  val userEntity: Persistence[UserEntity]

}
