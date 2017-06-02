package de.htwg.zeta.persistence.transientCache

import java.util.UUID

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
import models.document.Settings
import models.document.UserEntity


/**
 * Cache-Implementation of the PersistenceService.
 */
class PersistenceTransientCacheService extends PersistenceService {

  /** Persistence for the [[models.document.EventDrivenTask]] */
  override val eventDrivenTask = new TransientCachePersistence[UUID, EventDrivenTask]

  /** Persistence for the [[models.document.BondedTask]] */
  override val bondTask = new TransientCachePersistence[UUID, BondedTask]

  /** Persistence for the [[models.document.Generator]] */
  override val generator = new TransientCachePersistence[UUID, Generator]

  /** Persistence for the [[models.document.Filter]] */
  override val filter = new TransientCachePersistence[UUID, Filter]

  /** Persistence for the [[models.document.GeneratorImage]] */
  override val generatorImage = new TransientCachePersistence[UUID, GeneratorImage]

  /** Persistence for the [[models.document.FilterImage]] */
  override val filterImage = new TransientCachePersistence[UUID, FilterImage]

  /** Persistence for the [[models.document.Settings]] */
  override val settings = new TransientCachePersistence[UUID, Settings]

  /** Persistence for the [[models.document.MetaModelEntity]] */
  override val metaModelEntity = new TransientCachePersistence[UUID, MetaModelEntity]

  /** Persistence for the [[models.document.MetaModelRelease]] */
  override val metaModelRelease = new TransientCachePersistence[UUID, MetaModelRelease]

  /** Persistence for the [[models.document.ModelEntity]] */
  override val modelEntity = new TransientCachePersistence[UUID, ModelEntity]

  /** Persistence for the [[models.document.Log]] */
  override val log = new TransientCachePersistence[UUID, Log]

  /** Persistence for the [[models.document.UserEntity]] */
  override val user = new TransientCachePersistence[UUID, UserEntity]



}
