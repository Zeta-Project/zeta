package de.htwg.zeta.persistence.transient

import java.util.UUID
import javax.inject.Singleton

import scala.collection.concurrent.TrieMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.common.models.entity.EventDrivenTask
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.FilterImage
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.GraphicalDslRelease
import de.htwg.zeta.common.models.entity.Log
import de.htwg.zeta.common.models.entity.Settings
import de.htwg.zeta.common.models.entity.TimedTask
import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.common.models.project.GdslProject
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.persistence.general.AccessAuthorisationRepository
import de.htwg.zeta.persistence.general.BondedTaskRepository
import de.htwg.zeta.persistence.general.EntityRepository
import de.htwg.zeta.persistence.general.EventDrivenTaskRepository
import de.htwg.zeta.persistence.general.FilterImageRepository
import de.htwg.zeta.persistence.general.FilterRepository
import de.htwg.zeta.persistence.general.GeneratorImageRepository
import de.htwg.zeta.persistence.general.GeneratorRepository
import de.htwg.zeta.persistence.general.GraphicalDslInstanceRepository
import de.htwg.zeta.persistence.general.GraphicalDslReleaseRepository
import de.htwg.zeta.persistence.general.GdslProjectRepository
import de.htwg.zeta.persistence.general.LogRepository
import de.htwg.zeta.persistence.general.SettingsRepository
import de.htwg.zeta.persistence.general.TimedTaskRepository
import de.htwg.zeta.persistence.general.UserRepository

/** Cache implementation of [[de.htwg.zeta.persistence.general.EntityRepository]].
 *
 * @tparam E type of the entity
 */
sealed abstract class TransientEntityRepository[E <: Entity] extends EntityRepository[E] {

  private val cache: TrieMap[UUID, E] = TrieMap.empty

  override def create(entity: E): Future[E] = {
    if (cache.putIfAbsent(entity.id, entity).isEmpty) {
      Future.successful(entity)
    } else {
      Future.failed(new IllegalArgumentException("cant't create the entity, a entity with same id already exists"))
    }
  }

  override def read(id: UUID): Future[E] = {
    cache.get(id).fold[Future[E]] {
      Future.failed(new IllegalArgumentException("can't read the entity, a entity with the id doesn't exist"))
    } {
      Future.successful
    }
  }

  override def update(id: UUID, updateEntity: (E) => E): Future[E] = {
    read(id).flatMap { entity =>
      val updated = updateEntity(entity)
      cache.replace(entity.id, updated).get
      Future.successful(updated)
    }.recoverWith { case _ =>
      Future.failed(new IllegalArgumentException("can't update the entity, a entity with the id doesn't exist"))
    }
  }

  override def delete(id: UUID): Future[Unit] = {
    if (cache.remove(id).isDefined) {
      Future.successful(Unit)
    } else {
      Future.failed(new IllegalArgumentException("can't delete the entity, a entity with the id doesn't exist"))
    }
  }

  override def readAllIds(): Future[Set[UUID]] = {
    Future.successful(cache.keys.toSet)
  }

}


@Singleton
class TransientAccessAuthorisationRepository
  extends TransientEntityRepository[AccessAuthorisation]
    with AccessAuthorisationRepository

@Singleton
class TransientBondedTaskRepository
  extends TransientEntityRepository[BondedTask]
    with BondedTaskRepository

@Singleton
class TransientEventDrivenTaskRepository
  extends TransientEntityRepository[EventDrivenTask]
    with EventDrivenTaskRepository

@Singleton
class TransientFilterRepository
  extends TransientEntityRepository[Filter]
    with FilterRepository

@Singleton
class TransientFilterImageRepository
  extends TransientEntityRepository[FilterImage]
    with FilterImageRepository

@Singleton
class TransientGeneratorRepository
  extends TransientEntityRepository[Generator]
    with GeneratorRepository

@Singleton
class TransientGeneratorImageRepository
  extends TransientEntityRepository[GeneratorImage]
    with GeneratorImageRepository

@Singleton
class TransientLogRepository
  extends TransientEntityRepository[Log]
    with LogRepository

@Singleton
class TransientGraphicalDslRepository
  extends TransientEntityRepository[GdslProject]
    with GdslProjectRepository

@Singleton
class TransientGraphicalDslReleaseRepository
  extends TransientEntityRepository[GraphicalDslRelease]
    with GraphicalDslReleaseRepository

@Singleton
class TransientGraphicalDslInstanceRepository
  extends TransientEntityRepository[GraphicalDslInstance]
    with GraphicalDslInstanceRepository

@Singleton
class TransientSettingsRepository
  extends TransientEntityRepository[Settings]
    with SettingsRepository

@Singleton
class TransientTimedTaskRepository
  extends TransientEntityRepository[TimedTask]
    with TimedTaskRepository

@Singleton
class TransientUserRepository
  extends TransientEntityRepository[User]
    with UserRepository
