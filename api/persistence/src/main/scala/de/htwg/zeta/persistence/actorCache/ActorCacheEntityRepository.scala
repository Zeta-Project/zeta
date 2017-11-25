package de.htwg.zeta.persistence.actorCache

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.routing.ConsistentHashingPool
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import akka.util.Timeout
import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Create
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Delete
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Read
import de.htwg.zeta.persistence.actorCache.EntityCacheActor.Update
import de.htwg.zeta.persistence.general.AccessAuthorisationRepository
import de.htwg.zeta.persistence.general.BondedTaskRepository
import de.htwg.zeta.persistence.general.EntityRepository
import de.htwg.zeta.persistence.general.EventDrivenTaskRepository
import de.htwg.zeta.persistence.general.FilterImageRepository
import de.htwg.zeta.persistence.general.FilterRepository
import de.htwg.zeta.persistence.general.GeneratorImageRepository
import de.htwg.zeta.persistence.general.GeneratorRepository
import de.htwg.zeta.persistence.general.LogRepository
import de.htwg.zeta.persistence.general.MetaModelEntityRepository
import de.htwg.zeta.persistence.general.MetaModelReleaseRepository
import de.htwg.zeta.persistence.general.ModelEntityRepository
import de.htwg.zeta.persistence.general.SettingsRepository
import de.htwg.zeta.persistence.general.TimedTaskRepository
import de.htwg.zeta.persistence.general.UserRepository


/**
 * Actor Cache Implementation of EntityPersistence.
 */
sealed abstract class ActorCacheEntityRepository[E <: Entity](
    underlying: EntityRepository[E],
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    implicit val timeout: Timeout
)(implicit manifest: Manifest[E]) extends EntityRepository[E] {

  private def hashMapping: ConsistentHashMapping = {
    case Create(entity) => entity.id.hashCode
    case Read(id) => id.hashCode
    case Update(id, _) => id.hashCode
    case Delete(id) => id.hashCode
  }

  private val router: ActorRef = system.actorOf(
    ConsistentHashingPool(
      nrOfInstances = numberActorsPerEntityType,
      hashMapping = hashMapping
    ).props(
      EntityCacheActor.props(underlying, cacheDuration)
    ),
    entityTypeName
  )

  override def create(entity: E): Future[E] = {
    (router ? Create(entity)).flatMap {
      case Success(entity: E) => Future.successful(entity)
      case Failure(e) => Future.failed(e)
    }
  }

  override def read(id: UUID): Future[E] = {
    (router ? Read(id)).flatMap {
      case Success(entity: E) => Future.successful(entity)
      case Failure(e) => Future.failed(e)
    }
  }

  override def update(id: UUID, updateEntity: E => E): Future[E] = {
    (router ? Update(id, updateEntity)).flatMap {
      case Success(entity: E) => Future.successful(entity)
      case Failure(e) => Future.failed(e)
    }
  }

  override def delete(id: UUID): Future[Unit] = {
    (router ? Delete(id)).flatMap {
      case Success(()) => Future.successful(())
      case Failure(e) => Future.failed(e)
    }
  }

  override def readAllIds(): Future[Set[UUID]] = {
    underlying.readAllIds()
  }

}

@Singleton
class ActorCacheAccessAuthorisationRepository @Inject()(
    underlying: AccessAuthorisationRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with AccessAuthorisationRepository

@Singleton
class ActorCacheBondedTaskRepository @Inject()(
    underlying: BondedTaskRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with BondedTaskRepository

@Singleton
class ActorCacheEventDrivenTaskRepository @Inject()(
    underlying: EventDrivenTaskRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with EventDrivenTaskRepository

@Singleton
class ActorCacheFilterRepository @Inject()(
    underlying: FilterRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with FilterRepository

@Singleton
class ActorCacheFilterImageRepository @Inject()(
    underlying: FilterImageRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with FilterImageRepository

@Singleton
class ActorCacheGeneratorRepository @Inject()(
    underlying: GeneratorRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with GeneratorRepository

@Singleton
class ActorCacheGeneratorImageRepository @Inject()(
    underlying: GeneratorImageRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with GeneratorImageRepository

@Singleton
class ActorCacheLogRepository @Inject()(
    underlying: LogRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with LogRepository

@Singleton
class ActorCacheMetaModelEntityRepository @Inject()(
    underlying: MetaModelEntityRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with MetaModelEntityRepository


@Singleton
class ActorCacheMetaModelReleaseRepository @Inject()(
    underlying: MetaModelReleaseRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with MetaModelReleaseRepository


@Singleton
class ActorCacheModelEntityRepository @Inject()(
    underlying: ModelEntityRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with ModelEntityRepository

@Singleton
class ActorCacheSettingsRepository @Inject()(
    underlying: SettingsRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with SettingsRepository

@Singleton
class ActorCacheTimedTaskRepository @Inject()(
    underlying: TimedTaskRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with TimedTaskRepository

@Singleton
class ActorCacheUserRepository @Inject()(
    underlying: UserRepository,
    system: ActorSystem,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityRepository(underlying, system, numberActorsPerEntityType, cacheDuration, timeout)
  with UserRepository
