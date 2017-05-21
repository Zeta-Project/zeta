package de.htwg.zeta.persistence.transientCache

import de.htwg.zeta.persistence.general.PersistenceService
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
class PersistenceTransientCacheService extends PersistenceService {

  /** Persistence for the [[models.document.EventDrivenTask]] */
  override val eventDrivenTask = new TransientCachePersistence[EventDrivenTask]

  /** Persistence for the [[models.document.BondedTask]] */
  override val bondTask = new TransientCachePersistence[BondedTask]

  /** Persistence for the [[models.document.Generator]] */
  override val generator = new TransientCachePersistence[Generator]

  /** Persistence for the [[models.document.Filter]] */
  override val filter = new TransientCachePersistence[Filter]

  /** Persistence for the [[models.document.GeneratorImage]] */
  override val generatorImage = new TransientCachePersistence[GeneratorImage]

  /** Persistence for the [[models.document.FilterImage]] */
  override val filterImage = new TransientCachePersistence[FilterImage]

  /** Persistence for the [[models.document.Settings]] */
  override val settings = new TransientCachePersistence[Settings]

  /** Persistence for the [[models.document.MetaModelEntity]] */
  override val metaModelEntity = new TransientCachePersistence[MetaModelEntity]

  /** Persistence for the [[models.document.MetaModelRelease]] */
  override val metaModelRelease = new TransientCachePersistence[MetaModelRelease]

  /** Persistence for the [[models.document.ModelEntity]] */
  override val modelEntity = new TransientCachePersistence[ModelEntity]

  /** Persistence for the [[models.document.Log]] */
  override val log = new TransientCachePersistence[Log]

  /** Persistence for the [[models.document.PasswordInfoEntity]] */
  override val passwordInfoEntity = new TransientCachePersistence[PasswordInfoEntity]

  /** Persistence for the [[models.document.UserEntity]] */
  override val userEntity = new TransientCachePersistence[UserEntity]

}
