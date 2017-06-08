package de.htwg.zeta.persistence.mongo

import de.htwg.zeta.persistence.accessRestricted.AccessAuthorisation
import de.htwg.zeta.persistence.general.EntityVersion
import de.htwg.zeta.persistence.general.Persistence
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

class MongoRepository(uri: String, dbName: String) extends Repository {


  /** Persistence for AccessAuthorisation */
  override private[persistence] val accessAuthorisations: Persistence[AccessAuthorisation] =
    new MongoPersistence[AccessAuthorisation](uri, dbName, MongoHandler.accessAuthorisationHandler)

  /** Persistence for [[models.document.EventDrivenTask]] */
  override val eventDrivenTasks: Persistence[EventDrivenTask] =
    new MongoPersistence[EventDrivenTask](uri, dbName, MongoHandler.eventDrivenTaskHandler)

  /** Persistence for [[models.document.BondedTask]] */
  override val bondTasks: Persistence[BondedTask] =
    new MongoPersistence[BondedTask](uri, dbName, MongoHandler.bondedTaskHandler)

  /** Persistence for [[models.document.TimedTask]] */
  override val timedTasks: Persistence[TimedTask] =
    new MongoPersistence[TimedTask](uri, dbName, MongoHandler.timedTaskHandler)

  /** Persistence for [[models.document.Generator]] */
  override val generators: Persistence[Generator] =
    new MongoPersistence[Generator](uri, dbName, MongoHandler.generatorHandler)

  /** Persistence for [[models.document.Filter]] */
  override val filters: Persistence[Filter] =
    new MongoPersistence[Filter](uri, dbName, MongoHandler.filterHandler)

  /** Persistence for [[models.document.GeneratorImage]] */
  override val generatorImages: Persistence[GeneratorImage] =
    new MongoPersistence[GeneratorImage](uri, dbName, MongoHandler.generatorImageHandler)

  /** Persistence for [[models.document.FilterImage]] */
  override val filterImages: Persistence[FilterImage] =
    new MongoPersistence[FilterImage](uri, dbName, MongoHandler.filterImageHandler)

  /** Persistence for [[models.document.Settings]] */
  override val settings: Persistence[Settings] = null
  //  new MongoPersistence[Settings](uri, dbName, MongoHandler.settingsHandler)

  /** Persistence for [[models.document.MetaModelEntity]] */
  override val metaModelEntities: Persistence[MetaModelEntity] = null
  //  new MongoPersistence[MetaModelEntity](uri, dbName, MongoHandler.metaModelEntityHandler)

  /** Persistence for the metaModelReleases indices */
  override private[persistence] val metaModelReleasesIndices: Persistence[VersionIndex[Int]] = null
  //  new MongoPersistence[VersionIndex[Int]](uri, dbName, MongoHandler.versionIndexIntHandler)

  /** Persistence for the metaModelReleases versions */
  override private[persistence] val metaModelReleasesVersions: Persistence[EntityVersion[MetaModelRelease]] = null
  //  new MongoPersistence[EntityVersion[MetaModelRelease]](uri, dbName, MongoHandler.entityVersionMetaModelReleaseHandler)

  /** Persistence for [[models.document.ModelEntity]] */
  override val modelEntities: Persistence[ModelEntity] = null
  //  new MongoPersistence[ModelEntity](uri, dbName, MongoHandler.modelEntityHandler)

  /** Persistence for [[models.document.Log]] */
  override val logs: Persistence[Log] = null
  //  new MongoPersistence[Log](uri, dbName, MongoHandler.logHandler)

  /** Persistence for [[models.User]] */
  override val users: Persistence[User] =
    new MongoPersistence[User](uri, dbName, MongoHandler.userHandler)

  /** Persistence for the file indices */
  override private[persistence] val fileIndices: Persistence[VersionIndex[String]] = null
  //  new MongoPersistence[VersionIndex[String]](uri, dbName, MongoHandler.versionIndexStringHandler)

  /** Persistence for the file versions */
  override private[persistence] val fileVersions: Persistence[EntityVersion[File]] = null
  //  new MongoPersistence[EntityVersion[File]](uri, dbName, MongoHandler.entityVersionFileHandler)

}
