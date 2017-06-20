package de.htwg.zeta.persistence.transient

import de.htwg.zeta.common.models.entity
import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.CodeDocument
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
  override val accessAuthorisation = new TransientPersistence[AccessAuthorisation]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.EventDrivenTask]] */
  override val eventDrivenTask = new TransientPersistence[EventDrivenTask]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.BondedTask]] */
  override val bondedTask = new TransientPersistence[BondedTask]

  /** Persistence for [[de.htwg.zeta.common.models.entity.CodeDocument]] */
  override val codeDocument = new TransientPersistence[CodeDocument]

  /** Persistence for [[de.htwg.zeta.common.models.entity.TimedTask]] */
  override val timedTask = new TransientPersistence[TimedTask]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Generator]] */
  override val generator = new TransientPersistence[Generator]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Filter]] */
  override val filter = new TransientPersistence[Filter]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.GeneratorImage]] */
  override val generatorImage = new TransientPersistence[GeneratorImage]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.FilterImage]] */
  override val filterImage = new TransientPersistence[FilterImage]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Settings]] */
  override val settings = new TransientPersistence[Settings]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.MetaModelEntity]] */
  override val metaModelEntity = new TransientPersistence[MetaModelEntity]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.ModelEntity]] */
  override val modelEntity = new TransientPersistence[ModelEntity]

  /** Persistence for the [[de.htwg.zeta.common.models.entity.Log]] */
  override val log = new TransientPersistence[Log]

  /** Persistence for the [[User]] */
  override val user = new TransientPersistence[User]

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelRelease]] */
  override val metaModelRelease = new TransientPersistence[MetaModelRelease]

  /** Versioned Persistence for [[entity.File]] */
  override val file: FilePersistence = new TransientFilePersistence

  /** Persistence for [[com.mohiva.play.silhouette.api.LoginInfo]] */
  override val loginInfo: LoginInfoPersistence = new TransientLoginInfoPersistence

  /** Persistence for [[com.mohiva.play.silhouette.api.util.PasswordInfo]] */
  override val passwordInfo: PasswordInfoPersistence = new TransientPasswordInfoPersistence

}
