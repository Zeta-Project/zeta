package de.htwg.zeta.persistence.general

import models.entity.AccessAuthorisation
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

/** Combining all different Persistence types */
trait Repository {

  /** Persistence for [[models.entity.AccessAuthorisation]] */
  def accessAuthorisation: EntityPersistence[AccessAuthorisation]

  /** Persistence for [[models.entity.BondedTask]] */
  def bondedTask: EntityPersistence[BondedTask]

  /** Persistence for [[models.entity.EventDrivenTask]] */
  def eventDrivenTask: EntityPersistence[EventDrivenTask]

  /** Persistence for [[models.entity.Filter]] */
  def filter: EntityPersistence[Filter]

  /** Persistence for [[models.entity.FilterImage]] */
  def filterImage: EntityPersistence[FilterImage]

  /** Persistence for [[models.entity.Generator]] */
  def generator: EntityPersistence[Generator]

  /** Persistence for [[models.entity.GeneratorImage]] */
  def generatorImage: EntityPersistence[GeneratorImage]

  /** Persistence for [[models.entity.Log]] */
  def log: EntityPersistence[Log]

  /** Persistence for [[models.entity.MetaModelEntity]] */
  def metaModelEntity: EntityPersistence[MetaModelEntity]

  /** Persistence for [[models.entity.MetaModelRelease]] */
  def metaModelRelease: EntityPersistence[MetaModelRelease]

  /** Persistence for [[models.entity.ModelEntity]] */
  def modelEntity: EntityPersistence[ModelEntity]

  /** Persistence for [[models.entity.Settings]] */
  def settings: EntityPersistence[Settings]

  /** Persistence for [[models.entity.TimedTask]] */
  def timedTask: EntityPersistence[TimedTask]

  /** Persistence for [[models.entity.User]] */
  def user: EntityPersistence[User]

  /** Persistence for [[models.entity.File]] */
  def file: FilePersistence

  /** Persistence for [[com.mohiva.play.silhouette.api.LoginInfo]] */
  def loginInfo: LoginInfoPersistence

  /** Persistence for [[com.mohiva.play.silhouette.api.util.PasswordInfo]] */
  def passwordInfo: PasswordInfoPersistence

}
