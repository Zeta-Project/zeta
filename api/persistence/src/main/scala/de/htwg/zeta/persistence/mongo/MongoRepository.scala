package de.htwg.zeta.persistence.mongo

import de.htwg.zeta.persistence.accessRestricted.AccessAuthorisation
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.general.Repository
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

class MongoRepository(uri: String, dbName: String) extends Repository {


  /** Persistence for AccessAuthorisation */
  override private[persistence] val accessAuthorisations: EntityPersistence[AccessAuthorisation] =
    new MongoEntityPersistence[AccessAuthorisation](uri, dbName, MongoHandler.accessAuthorisationHandler)

  /** Persistence for [[models.entity.EventDrivenTask]] */
  override val eventDrivenTasks: EntityPersistence[EventDrivenTask] =
    new MongoEntityPersistence[EventDrivenTask](uri, dbName, MongoHandler.eventDrivenTaskHandler)

  /** Persistence for [[models.entity.BondedTask]] */
  override val bondTasks: EntityPersistence[BondedTask] =
    new MongoEntityPersistence[BondedTask](uri, dbName, MongoHandler.bondedTaskHandler)

  /** Persistence for [[models.entity.TimedTask]] */
  override val timedTasks: EntityPersistence[TimedTask] =
    new MongoEntityPersistence[TimedTask](uri, dbName, MongoHandler.timedTaskHandler)

  /** Persistence for [[models.entity.Generator]] */
  override val generators: EntityPersistence[Generator] =
    new MongoEntityPersistence[Generator](uri, dbName, MongoHandler.generatorHandler)

  /** Persistence for [[models.entity.Filter]] */
  override val filters: EntityPersistence[Filter] =
    new MongoEntityPersistence[Filter](uri, dbName, MongoHandler.filterHandler)

  /** Persistence for [[models.entity.GeneratorImage]] */
  override val generatorImages: EntityPersistence[GeneratorImage] =
    new MongoEntityPersistence[GeneratorImage](uri, dbName, MongoHandler.generatorImageHandler)

  /** Persistence for [[models.entity.FilterImage]] */
  override val filterImages: EntityPersistence[FilterImage] =
    new MongoEntityPersistence[FilterImage](uri, dbName, MongoHandler.filterImageHandler)

  /** Persistence for [[models.entity.Settings]] */
  override val settings: EntityPersistence[Settings] =
    new MongoEntityPersistence[Settings](uri, dbName, MongoHandler.settingsHandler)

  /** Persistence for [[models.entity.MetaModelEntity]] */
  override val metaModelEntities: EntityPersistence[MetaModelEntity] =
    new MongoEntityPersistence[MetaModelEntity](uri, dbName, MongoHandler.metaModelEntityHandler)

  /** Persistence for [[models.entity.MetaModelRelease]] */
  override val metaModelReleases: EntityPersistence[MetaModelRelease] =
    new MongoEntityPersistence[MetaModelRelease](uri, dbName, null) // TODO

  /** Persistence for [[models.entity.ModelEntity]] */
  override val modelEntities: EntityPersistence[ModelEntity] =
    new MongoEntityPersistence[ModelEntity](uri, dbName, MongoHandler.modelEntityHandler)

  /** Persistence for [[models.entity.Log]] */
  override val logs: EntityPersistence[Log] =
    new MongoEntityPersistence[Log](uri, dbName, MongoHandler.logHandler)

  /** Persistence for [[User]] */
  override val users: EntityPersistence[User] =
    new MongoEntityPersistence[User](uri, dbName, MongoHandler.userHandler)

  /** Versioned Persistence for [[models.file.File]] */
  override val files: FilePersistence = new MongoFilePersistence(uri, dbName)

}
