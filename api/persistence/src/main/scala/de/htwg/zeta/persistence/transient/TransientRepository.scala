package de.htwg.zeta.persistence.transient

import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.PasswordInfoPersistence
import de.htwg.zeta.persistence.general.Repository
import models.entity
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


/** Cache-Implementation of the PersistenceService. */
class TransientRepository extends Repository {

  /** Persistence for AccessAuthorisation */
  override val accessAuthorisation = new TransientPersistence[AccessAuthorisation]

  /** Persistence for the [[models.entity.EventDrivenTask]] */
  override val eventDrivenTask = new TransientPersistence[EventDrivenTask]

  /** Persistence for the [[models.entity.BondedTask]] */
  override val bondedTask = new TransientPersistence[BondedTask]

  /** Persistence for [[models.entity.TimedTask]] */
  override val timedTask = new TransientPersistence[TimedTask]

  /** Persistence for the [[models.entity.Generator]] */
  override val generator = new TransientPersistence[Generator]

  /** Persistence for the [[models.entity.Filter]] */
  override val filter = new TransientPersistence[Filter]

  /** Persistence for the [[models.entity.GeneratorImage]] */
  override val generatorImage = new TransientPersistence[GeneratorImage]

  /** Persistence for the [[models.entity.FilterImage]] */
  override val filterImage = new TransientPersistence[FilterImage]

  /** Persistence for the [[models.entity.Settings]] */
  override val settings = new TransientPersistence[Settings]

  /** Persistence for the [[models.entity.MetaModelEntity]] */
  override val metaModelEntity = new TransientPersistence[MetaModelEntity]

  /** Persistence for the [[models.entity.ModelEntity]] */
  override val modelEntity = new TransientPersistence[ModelEntity]

  /** Persistence for the [[models.entity.Log]] */
  override val log = new TransientPersistence[Log]

  /** Persistence for the [[User]] */
  override val user = new TransientPersistence[User]

  /** Persistence for [[models.entity.MetaModelRelease]] */
  override val metaModelRelease = new TransientPersistence[MetaModelRelease]

  /** Versioned Persistence for [[entity.File]] */
  override val file: FilePersistence = null // TODO

  /** Persistence for [[com.mohiva.play.silhouette.api.LoginInfo]] */
  override val loginInfo: LoginInfoPersistence = null // TODO

  /** Persistence for [[com.mohiva.play.silhouette.api.util.PasswordInfo]] */
  override val passwordInfo: PasswordInfoPersistence = null // TODO
  
}
