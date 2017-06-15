package de.htwg.zeta.persistence.general

import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.EventDrivenTask
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

/** Combining all different Persistence types */
trait Repository {

  /** Persistence for [[de.htwg.zeta.common.models.entity.AccessAuthorisation]] */
  def accessAuthorisation: EntityPersistence[AccessAuthorisation]

  /** Persistence for [[de.htwg.zeta.common.models.entity.BondedTask]] */
  def bondedTask: EntityPersistence[BondedTask]

  /** Persistence for [[de.htwg.zeta.common.models.entity.EventDrivenTask]] */
  def eventDrivenTask: EntityPersistence[EventDrivenTask]

  /** Persistence for [[de.htwg.zeta.common.models.entity.Filter]] */
  def filter: EntityPersistence[Filter]

  /** Persistence for [[de.htwg.zeta.common.models.entity.FilterImage]] */
  def filterImage: EntityPersistence[FilterImage]

  /** Persistence for [[de.htwg.zeta.common.models.entity.Generator]] */
  def generator: EntityPersistence[Generator]

  /** Persistence for [[de.htwg.zeta.common.models.entity.GeneratorImage]] */
  def generatorImage: EntityPersistence[GeneratorImage]

  /** Persistence for [[de.htwg.zeta.common.models.entity.Log]] */
  def log: EntityPersistence[Log]

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelEntity]] */
  def metaModelEntity: EntityPersistence[MetaModelEntity]

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelRelease]] */
  def metaModelRelease: EntityPersistence[MetaModelRelease]

  /** Persistence for [[de.htwg.zeta.common.models.entity.ModelEntity]] */
  def modelEntity: EntityPersistence[ModelEntity]

  /** Persistence for [[de.htwg.zeta.common.models.entity.Settings]] */
  def settings: EntityPersistence[Settings]

  /** Persistence for [[de.htwg.zeta.common.models.entity.TimedTask]] */
  def timedTask: EntityPersistence[TimedTask]

  /** Persistence for [[de.htwg.zeta.common.models.entity.User]] */
  def user: EntityPersistence[User]

  /** Persistence for [[de.htwg.zeta.common.models.entity.File]] */
  def file: FilePersistence

  /** Persistence for [[com.mohiva.play.silhouette.api.LoginInfo]] */
  def loginInfo: LoginInfoPersistence

  /** Persistence for [[com.mohiva.play.silhouette.api.util.PasswordInfo]] */
  def passwordInfo: PasswordInfoPersistence

}
