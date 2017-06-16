package de.htwg.zeta.persistence.actorCache

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
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
 * TODO.
 */
class ActorCacheRepository(underlying: Repository) extends Repository {

  private val system = ActorSystem("ActorCacheRepository")

  /** The minimum duration to cache the entities. */
  private val cacheDuration = Duration(10, TimeUnit.MINUTES) // scalastyle:ignore magic.number

  /** Number of actors for each persistence. */
  private val nrOfInstances = 10

  /** Persistence for [[de.htwg.zeta.common.models.entity.AccessAuthorisation]] */
  override def accessAuthorisation: EntityPersistence[AccessAuthorisation] = {
    new ActorCacheEntityPersistence(system, underlying.accessAuthorisation, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.BondedTask]] */
  override def bondedTask: EntityPersistence[BondedTask] = {
    new ActorCacheEntityPersistence(system, underlying.bondedTask, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.EventDrivenTask]] */
  override def eventDrivenTask: EntityPersistence[EventDrivenTask] = {
    new ActorCacheEntityPersistence(system, underlying.eventDrivenTask, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.Filter]] */
  override def filter: EntityPersistence[Filter] = {
    new ActorCacheEntityPersistence(system, underlying.filter, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.FilterImage]] */
  override def filterImage: EntityPersistence[FilterImage] = {
    new ActorCacheEntityPersistence(system, underlying.filterImage, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.Generator]] */
  override def generator: EntityPersistence[Generator] = {
    new ActorCacheEntityPersistence(system, underlying.generator, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.GeneratorImage]] */
  override def generatorImage: EntityPersistence[GeneratorImage] = {
    new ActorCacheEntityPersistence(system, underlying.generatorImage, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.Log]] */
  override def log: EntityPersistence[Log] = {
    new ActorCacheEntityPersistence(system, underlying.log, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelEntity]] */
  override def metaModelEntity: EntityPersistence[MetaModelEntity] = {
    new ActorCacheEntityPersistence(system, underlying.metaModelEntity, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.MetaModelRelease]] */
  override def metaModelRelease: EntityPersistence[MetaModelRelease] = {
    new ActorCacheEntityPersistence(system, underlying.metaModelRelease, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.ModelEntity]] */
  override def modelEntity: EntityPersistence[ModelEntity] = {
    new ActorCacheEntityPersistence(system, underlying.modelEntity, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.Settings]] */
  override def settings: EntityPersistence[Settings] = {
    new ActorCacheEntityPersistence(system, underlying.settings, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.TimedTask]] */
  override def timedTask: EntityPersistence[TimedTask] = {
    new ActorCacheEntityPersistence(system, underlying.timedTask, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.User]] */
  override def user: EntityPersistence[User] = {
    new ActorCacheEntityPersistence(system, underlying.user, nrOfInstances, cacheDuration)
  }

  /** Persistence for [[de.htwg.zeta.common.models.entity.File]] */
  override def file: FilePersistence = {
    null // TODO
  }

  /** Persistence for [[com.mohiva.play.silhouette.api.LoginInfo]] */
  override def loginInfo: LoginInfoPersistence = {
    null // TODO
  }

  /** Persistence for [[com.mohiva.play.silhouette.api.util.PasswordInfo]] */
  override def passwordInfo: PasswordInfoPersistence = {
    null // TODO
  }

}
