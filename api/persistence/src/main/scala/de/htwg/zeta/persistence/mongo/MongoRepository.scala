package de.htwg.zeta.persistence.mongo

import de.htwg.zeta.persistence.accessRestricted.AccessAuthorisation
import de.htwg.zeta.persistence.general.EntityVersion
import de.htwg.zeta.persistence.general.Persistence
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

class MongoRepository(uri: String, dbName: String) extends Repository {


  /** Persistence for AccessAuthorisation */
  override private[persistence] val accessAuthorisations: Persistence[AccessAuthorisation] =
    new MongoPersistence[AccessAuthorisation](uri, dbName, MongoHandler.accessAuthorisationHandler)

  /** Persistence for [[models.entity.EventDrivenTask]] */
  override val eventDrivenTasks: Persistence[EventDrivenTask] =
    new MongoPersistence[EventDrivenTask](uri, dbName, MongoHandler.eventDrivenTaskHandler)

  /** Persistence for [[models.entity.BondedTask]] */
  override val bondTasks: Persistence[BondedTask] =
    new MongoPersistence[BondedTask](uri, dbName, MongoHandler.bondedTaskHandler)

  /** Persistence for [[models.entity.TimedTask]] */
  override val timedTasks: Persistence[TimedTask] =
    new MongoPersistence[TimedTask](uri, dbName, MongoHandler.timedTaskHandler)

  /** Persistence for [[models.entity.Generator]] */
  override val generators: Persistence[Generator] =
    new MongoPersistence[Generator](uri, dbName, MongoHandler.generatorHandler)

  /** Persistence for [[models.entity.Filter]] */
  override val filters: Persistence[Filter] =
    new MongoPersistence[Filter](uri, dbName, MongoHandler.filterHandler)

  /** Persistence for [[models.entity.GeneratorImage]] */
  override val generatorImages: Persistence[GeneratorImage] =
    new MongoPersistence[GeneratorImage](uri, dbName, MongoHandler.generatorImageHandler)

  /** Persistence for [[models.entity.FilterImage]] */
  override val filterImages: Persistence[FilterImage] =
    new MongoPersistence[FilterImage](uri, dbName, MongoHandler.filterImageHandler)

  /** Persistence for [[models.entity.Settings]] */
  override val settings: Persistence[Settings] =
    new MongoPersistence[Settings](uri, dbName, MongoHandler.settingsHandler)

  /** Persistence for [[models.entity.MetaModelEntity]] */
  override val metaModelEntities: Persistence[MetaModelEntity] =
    new MongoPersistence[MetaModelEntity](uri, dbName, MongoHandler.metaModelEntityHandler)

  /** Persistence for the metaModelReleases indices */
  override private[persistence] val metaModelReleasesIndices: Persistence[VersionIndex] =
    new MongoPersistence[VersionIndex](uri, dbName, MongoHandler.versionIndexIntHandler)

  /** Persistence for the metaModelReleases versions */
  override private[persistence] val metaModelReleasesVersions: Persistence[EntityVersion[MetaModelRelease]] =
    new MongoPersistence[EntityVersion[MetaModelRelease]](uri, dbName, MongoHandler.entityVersionReleaseHandler)

  /** Persistence for [[models.entity.ModelEntity]] */
  override val modelEntities: Persistence[ModelEntity] =
    new MongoPersistence[ModelEntity](uri, dbName, MongoHandler.modelEntityHandler)

  /** Persistence for [[models.entity.Log]] */
  override val logs: Persistence[Log] =
    new MongoPersistence[Log](uri, dbName, MongoHandler.logHandler)

  /** Persistence for [[User]] */
  override val users: Persistence[User] =
    new MongoPersistence[User](uri, dbName, MongoHandler.userHandler)

  /** Persistence for the file indices */
  override private[persistence] val fileIndices: Persistence[VersionIndex] =
    new MongoPersistence[VersionIndex](uri, dbName, MongoHandler.versionIndexStringHandler)

  /** Persistence for the file versions */
  override private[persistence] val fileVersions: Persistence[EntityVersion[File]] =
    new MongoPersistence[EntityVersion[File]](uri, dbName, MongoHandler.entityVersionReleaseHandler)

}
