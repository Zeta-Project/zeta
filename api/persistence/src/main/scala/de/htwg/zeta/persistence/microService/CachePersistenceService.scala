package de.htwg.zeta.persistence.microService

import de.htwg.zeta.persistence.dbaccess.CachePersistence
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
 * Cache-Implementation of the PersistenceService.
 */
class CachePersistenceService extends PersistenceService {

  /** Persistence for the [[models.document.EventDrivenTask]] */
  override val eventDrivenTask = new CachePersistence[EventDrivenTask]

  /** Persistence for the [[models.document.BondedTask]] */
  override val bondTask = new CachePersistence[BondedTask]

  /** Persistence for the [[models.document.Generator]] */
  override val generator = new CachePersistence[Generator]

  /** Persistence for the [[models.document.Filter]] */
  override val filter = new CachePersistence[Filter]

  /** Persistence for the [[models.document.GeneratorImage]] */
  override val generatorImage = new CachePersistence[GeneratorImage]

  /** Persistence for the [[models.document.FilterImage]] */
  override val filterImage = new CachePersistence[FilterImage]

  /** Persistence for the [[models.document.Settings]] */
  override val settings = new CachePersistence[Settings]

  /** Persistence for the [[models.document.MetaModelEntity]] */
  override val metaModelEntity = new CachePersistence[MetaModelEntity]

  /** Persistence for the [[models.document.MetaModelRelease]] */
  override val metaModelRelease = new CachePersistence[MetaModelRelease]

  /** Persistence for the [[models.document.ModelEntity]] */
  override val modelEntity = new CachePersistence[ModelEntity]

  /** Persistence for the [[models.document.Log]] */
  override val log = new CachePersistence[Log]

  /** Persistence for the [[models.document.PasswordInfoEntity]] */
  override val passwordInfoEntity = new CachePersistence[PasswordInfoEntity]

  /** Persistence for the [[models.document.UserEntity]] */
  override val userEntity = new CachePersistence[UserEntity]

}
