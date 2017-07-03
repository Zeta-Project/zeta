package de.htwg.zeta.persistence.actorCache

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import akka.util.Timeout
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
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.general.PasswordInfoPersistence
import de.htwg.zeta.persistence.general.Repository

/**
 * High scalable distributed cache using actors. Each message is routed to an specific actor using the hashed entity-id of each message. Requests for the same
 * ids are always routed to the same actor. This additionally solves the problem of multiple simultaneously accesses to the same entity, since the actor, which
 * is responsible for an id, handles all messages successively.
 */
class ActorCacheRepository(underlying: Repository) extends Repository {

  private val system = ActorSystem("ActorCacheRepository")

  /** Number of actors for each persistence. */
  private val numberActorsPerEntityType = 10

  /** The minimum duration to cache the entities. */
  private val cacheDuration = Duration(10, TimeUnit.MINUTES) // scalastyle:ignore magic.number

  /** The time an actor has for a properly respond. */
  private val timeout: Timeout = Duration(10, TimeUnit.SECONDS) // scalastyle:ignore magic.number

  /** Persistence for [[de.htwg.zeta.common.models.entity.AccessAuthorisation]] */
  override val accessAuthorisation: EntityPersistence[AccessAuthorisation] = {
    new ActorCacheEntityPersistence(system, underlying.accessAuthorisation, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.BondedTask]] */
  override val bondedTask: EntityPersistence[BondedTask] = {
    new ActorCacheEntityPersistence(system, underlying.bondedTask, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.EventDrivenTask]] */
  override val eventDrivenTask: EntityPersistence[EventDrivenTask] = {
    new ActorCacheEntityPersistence(system, underlying.eventDrivenTask, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.Filter]] */
  override val filter: EntityPersistence[Filter] = {
    new ActorCacheEntityPersistence(system, underlying.filter, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.FilterImage]] */
  override val filterImage: EntityPersistence[FilterImage] = {
    new ActorCacheEntityPersistence(system, underlying.filterImage, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.Generator]] */
  override val generator: EntityPersistence[Generator] = {
    new ActorCacheEntityPersistence(system, underlying.generator, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.GeneratorImage]] */
  override val generatorImage: EntityPersistence[GeneratorImage] = {
    new ActorCacheEntityPersistence(system, underlying.generatorImage, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.Log]] */
  override val log: EntityPersistence[Log] = {
    new ActorCacheEntityPersistence(system, underlying.log, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelEntity]] */
  override val metaModelEntity: EntityPersistence[MetaModelEntity] = {
    new ActorCacheEntityPersistence(system, underlying.metaModelEntity, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelRelease]] */
  override val metaModelRelease: EntityPersistence[MetaModelRelease] = {
    new ActorCacheEntityPersistence(system, underlying.metaModelRelease, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.ModelEntity]] */
  override val modelEntity: EntityPersistence[ModelEntity] = {
    new ActorCacheEntityPersistence(system, underlying.modelEntity, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.Settings]] */
  override val settings: EntityPersistence[Settings] = {
    new ActorCacheEntityPersistence(system, underlying.settings, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.TimedTask]] */
  override val timedTask: EntityPersistence[TimedTask] = {
    new ActorCacheEntityPersistence(system, underlying.timedTask, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.User]] */
  override val user: EntityPersistence[User] = {
    new ActorCacheEntityPersistence(system, underlying.user, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.File]] */
  override val file: FilePersistence = {
    new ActorCacheFilePersistence(system, underlying.file, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[com.mohiva.play.silhouette.api.LoginInfo]] */
  override val loginInfo: LoginInfoPersistence = {
    new ActorCacheLoginInfoPersistence(system, underlying.loginInfo, numberActorsPerEntityType, cacheDuration, timeout)
  }

  /** Persistence for [[com.mohiva.play.silhouette.api.util.PasswordInfo]] */
  override val passwordInfo: PasswordInfoPersistence = {
    new ActorCachePasswordInfoPersistence(system, underlying.passwordInfo, numberActorsPerEntityType, cacheDuration, timeout)
  }

}
