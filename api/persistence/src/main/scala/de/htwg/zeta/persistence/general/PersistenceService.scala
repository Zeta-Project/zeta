package de.htwg.zeta.persistence.general

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
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
import models.document.PasswordInfoEntity
import models.document.Settings
import models.document.UserEntity

/**
 * Persistence Implementation for the different types of documents.
 */
trait PersistenceService {

  /** Persistence for [[models.document.EventDrivenTask]] */
  val eventDrivenTask: Persistence[UUID, EventDrivenTask]

  /** Persistence for [[models.document.BondedTask]] */
  val bondTask: Persistence[UUID, BondedTask]

  /** Persistence for [[models.document.Generator]] */
  val generator: Persistence[UUID, Generator]

  /** Persistence for [[models.document.Filter]] */
  val filter: Persistence[UUID, Filter]

  /** Persistence for [[models.document.GeneratorImage]] */
  val generatorImage: Persistence[UUID, GeneratorImage]

  /** Persistence for [[models.document.FilterImage]] */
  val filterImage: Persistence[UUID, FilterImage]

  /** Persistence for [[models.document.Settings]] */
  val settings: Persistence[UUID, Settings]

  /** Persistence for [[models.document.MetaModelEntity]] */
  val metaModelEntity: Persistence[UUID, MetaModelEntity]

  /** Persistence for [[models.document.MetaModelRelease]] */
  val metaModelRelease: Persistence[UUID, MetaModelRelease]

  /** Persistence for [[models.document.ModelEntity]] */
  val modelEntity: Persistence[UUID, ModelEntity]

  /** Persistence for [[models.document.Log]] */
  val log: Persistence[UUID, Log]

  /** Persistence for [[models.document.PasswordInfoEntity]] */
  val passwordInfoEntity: Persistence[UUID, PasswordInfoEntity]

  /** Persistence for [[models.User]] */
  val user: Persistence[UUID, User]

  /** Persistence for [[com.mohiva.play.silhouette.api.LoginInfo]] */
  val loginInfo: Persistence[LoginInfo, UUID]

}
