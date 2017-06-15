package de.htwg.zeta.persistence.mongo

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.EventDrivenTask
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.FilterImage
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.Log
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.entity.Settings
import de.htwg.zeta.common.models.entity.TimedTask
import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.PasswordInfoPersistence
import de.htwg.zeta.persistence.general.Repository
import reactivemongo.api.DefaultDB
import reactivemongo.api.MongoDriver

class MongoRepository(uri: String, dbName: String) extends Repository {

  private val database: Future[DefaultDB] = Future.fromTry(MongoDriver().connection(uri)).flatMap(_.database(dbName))

  /** Persistence for AccessAuthorisation */
  override val accessAuthorisation: EntityPersistence[AccessAuthorisation] =
    new MongoEntityPersistence[AccessAuthorisation](database, MongoHandler.accessAuthorisationHandler)

  /** Persistence for [[de.htwg.zeta.common.models.entity.EventDrivenTask]] */
  override val eventDrivenTask: EntityPersistence[EventDrivenTask] =
    new MongoEntityPersistence[EventDrivenTask](database, MongoHandler.eventDrivenTaskHandler)

  /** Persistence for [[de.htwg.zeta.common.models.entity.BondedTask]] */
  override val bondedTask: EntityPersistence[BondedTask] =
    new MongoEntityPersistence[BondedTask](database, MongoHandler.bondedTaskHandler)

  /** Persistence for [[de.htwg.zeta.common.models.entity.TimedTask]] */
  override val timedTask: EntityPersistence[TimedTask] =
    new MongoEntityPersistence[TimedTask](database, MongoHandler.timedTaskHandler)

  /** Persistence for [[de.htwg.zeta.common.models.entity.Generator]] */
  override val generator: EntityPersistence[Generator] =
    new MongoEntityPersistence[Generator](database, MongoHandler.generatorHandler)

  /** Persistence for [[de.htwg.zeta.common.models.entity.Filter]] */
  override val filter: EntityPersistence[Filter] =
    new MongoEntityPersistence[Filter](database, MongoHandler.filterHandler)

  /** Persistence for [[de.htwg.zeta.common.models.entity.GeneratorImage]] */
  override val generatorImage: EntityPersistence[GeneratorImage] =
    new MongoEntityPersistence[GeneratorImage](database, MongoHandler.generatorImageHandler)

  /** Persistence for [[de.htwg.zeta.common.models.entity.FilterImage]] */
  override val filterImage: EntityPersistence[FilterImage] =
    new MongoEntityPersistence[FilterImage](database, MongoHandler.filterImageHandler)

  /** Persistence for [[de.htwg.zeta.common.models.entity.Settings]] */
  override val settings: EntityPersistence[Settings] =
    new MongoEntityPersistence[Settings](database, MongoHandler.settingsHandler)

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelEntity]] */
  override val metaModelEntity: EntityPersistence[MetaModelEntity] =
    new MongoEntityPersistence[MetaModelEntity](database, MongoHandler.metaModelEntityHandler)

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelRelease]] */
  override val metaModelRelease: EntityPersistence[MetaModelRelease] =
    new MongoEntityPersistence[MetaModelRelease](database, MongoHandler.metaModelReleaseHandler)

  /** Persistence for [[de.htwg.zeta.common.models.entity.ModelEntity]] */
  override val modelEntity: EntityPersistence[ModelEntity] =
    new MongoEntityPersistence[ModelEntity](database, MongoHandler.modelEntityHandler)

  /** Persistence for [[de.htwg.zeta.common.models.entity.Log]] */
  override val log: EntityPersistence[Log] =
    new MongoEntityPersistence[Log](database, MongoHandler.logHandler)

  /** Persistence for [[User]] */
  override val user: EntityPersistence[User] =
    new MongoEntityPersistence[User](database, MongoHandler.userHandler)

  /** Versioned Persistence for [[File]] */
  override val file: FilePersistence =
    new MongoFilePersistence(database)

  /** Persistence for [[com.mohiva.play.silhouette.api.LoginInfo]] */
  override val loginInfo: LoginInfoPersistence =
    new MongoLoginInfoPersistence(database)

  /** Persistence for [[com.mohiva.play.silhouette.api.util.PasswordInfo]] */
  override val passwordInfo: PasswordInfoPersistence =
    new MongoPasswordInfoPersistence(database)

}
