package de.htwg.zeta.persistence.transient

import de.htwg.zeta.common.models.entity
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
import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.PasswordInfoPersistence
import de.htwg.zeta.persistence.general.Repository


/** Cache-Implementation of the PersistenceService. */
class TransientRepository extends Repository {

  /** Persistence for AccessAuthorisation */
  override val accessAuthorisation = new TransientEntityPersistence[AccessAuthorisation]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.EventDrivenTask]] */
  override val eventDrivenTask = new TransientEntityPersistence[EventDrivenTask]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.BondedTask]] */
  override val bondedTask = new TransientEntityPersistence[BondedTask]

  /** Persistence for [[de.htwg.zeta.common.models.entity.TimedTask]] */
  override val timedTask = new TransientEntityPersistence[TimedTask]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Generator]] */
  override val generator = new TransientEntityPersistence[Generator]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Filter]] */
  override val filter = new TransientEntityPersistence[Filter]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.GeneratorImage]] */
  override val generatorImage = new TransientEntityPersistence[GeneratorImage]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.FilterImage]] */
  override val filterImage = new TransientEntityPersistence[FilterImage]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Settings]] */
  override val settings = new TransientEntityPersistence[Settings]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.MetaModelEntity]] */
  override val metaModelEntity = new TransientEntityPersistence[MetaModelEntity]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.ModelEntity]] */
  override val modelEntity = new TransientEntityPersistence[ModelEntity]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Log]] */
  override val log = new TransientEntityPersistence[Log]

  /** Persistence for the [[User]] */
  override val user = new TransientEntityPersistence[User]

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelRelease]] */
  override val metaModelRelease = new TransientEntityPersistence[MetaModelRelease]

  /** Versioned Persistence for [[entity.File]] */
  override val file: FilePersistence = new TransientFilePersistence

  /** Persistence for [[com.mohiva.play.silhouette.api.LoginInfo]] */
  override val loginInfo: LoginInfoPersistence = new TransientLoginInfoPersistence

  /** Persistence for [[com.mohiva.play.silhouette.api.util.PasswordInfo]] */
  override val passwordInfo: PasswordInfoPersistence = new TransientPasswordInfoPersistence

}
