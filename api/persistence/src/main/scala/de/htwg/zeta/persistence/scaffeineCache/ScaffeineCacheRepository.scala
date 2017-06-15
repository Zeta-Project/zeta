package de.htwg.zeta.persistence.scaffeineCache

import de.htwg.zeta.persistence.general.Repository

/** ScaffeineCache Implementation of the Repository. Caches the data for a short
 * time and requires an underlying repository saving permanently.
 */
class ScaffeineCacheRepository(underlying: Repository) extends Repository {

  /** Persistence for [[de.htwg.zeta.common.models.entity.AccessAuthorisation]] */
  override val accessAuthorisation = new ScaffeineCacheEntityPersistence(underlying.accessAuthorisation)

  /** Persistence for [[de.htwg.zeta.common.models.entity.BondedTask]] */
  override val bondedTask = new ScaffeineCacheEntityPersistence(underlying.bondedTask)

  /** Persistence for [[de.htwg.zeta.common.models.entity.EventDrivenTask]] */
  override val eventDrivenTask = new ScaffeineCacheEntityPersistence(underlying.eventDrivenTask)

  /** Persistence for [[de.htwg.zeta.common.models.entity.Filter]] */
  override val filter = new ScaffeineCacheEntityPersistence(underlying.filter)

  /** Persistence for [[de.htwg.zeta.common.models.entity.FilterImage]] */
  override val filterImage = new ScaffeineCacheEntityPersistence(underlying.filterImage)

  /** Persistence for [[de.htwg.zeta.common.models.entity.Generator]] */
  override val generator = new ScaffeineCacheEntityPersistence(underlying.generator)

  /** Persistence for [[de.htwg.zeta.common.models.entity.GeneratorImage]] */
  override val generatorImage = new ScaffeineCacheEntityPersistence(underlying.generatorImage)

  /** Persistence for [[de.htwg.zeta.common.models.entity.Log]] */
  override val log = new ScaffeineCacheEntityPersistence(underlying.log)

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelEntity]] */
  override val metaModelEntity = new ScaffeineCacheEntityPersistence(underlying.metaModelEntity)

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelRelease]] */
  override val metaModelRelease = new ScaffeineCacheEntityPersistence(underlying.metaModelRelease)

  /** Persistence for [[de.htwg.zeta.common.models.entity.ModelEntity]] */
  override val modelEntity = new ScaffeineCacheEntityPersistence(underlying.modelEntity)

  /** Persistence for [[de.htwg.zeta.common.models.entity.Settings]] */
  override val settings = new ScaffeineCacheEntityPersistence(underlying.settings)

  /** Persistence for [[de.htwg.zeta.common.models.entity.TimedTask]] */
  override val timedTask = new ScaffeineCacheEntityPersistence(underlying.timedTask)

  /** Persistence for [[de.htwg.zeta.common.models.entity.User]] */
  override val user = new ScaffeineCacheEntityPersistence(underlying.user)

  /** Persistence for [[de.htwg.zeta.common.models.entity.File]] */
  override val file = null // TODO new ScaffeineCacheFilePersistence(underlying.file)

  /** Persistence for [[com.mohiva.play.silhouette.api.LoginInfo]] */
  override val loginInfo = null // TODO new ScaffeineCacheLoginInfoPersistence(underlying.loginInfo)

  /** Persistence for [[com.mohiva.play.silhouette.api.util.PasswordInfo]] */
  override val passwordInfo = null // TODO new ScaffeineCachePasswordInfoPersistence(underlying.passwordInfo)

}
