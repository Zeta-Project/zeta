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
import de.htwg.zeta.common.models.entity.Log
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.entity.Settings
import de.htwg.zeta.common.models.entity.TimedTask
import de.htwg.zeta.common.models.entity.User
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

/** Cache implementation of [[EntityRepository]].
 *
 * @tparam E type of the entity
 */
@Singleton
class TransientEntityRepository[E <: Entity] extends EntityRepository[E] { // scalastyle:ignore

  private val cache: TrieMap[UUID, E] = TrieMap.empty

  /** Create a new entity.
   *
   * @param entity the entity to save
   * @return Future, with the created entity
   */
  override def create(entity: E): Future[E] = {
    if (cache.putIfAbsent(entity.id, entity).isEmpty) {
      Future.successful(entity)
    } else {
      Future.failed(new IllegalArgumentException("cant't create the entity, a entity with same id already exists"))
    }
  }

  /** Get a single entity.
   *
   * @param id The id of the entity
   * @return Future which resolve with the entity and can fail
   */
  override def read(id: UUID): Future[E] = {
    cache.get(id).fold[Future[E]] {
      Future.failed(new IllegalArgumentException("can't read the entity, a entity with the id doesn't exist"))
    } {
      Future.successful
    }
  }

  /** Update a entity.
   *
   * @param id           The id of the entity
   * @param updateEntity Function, to build the updated entity from the existing
   * @return Future containing the updated entity
   */
  override def update(id: UUID, updateEntity: (E) => E): Future[E] = {
    read(id).flatMap { entity =>
      val updated = updateEntity(entity)
      cache.replace(entity.id, updated).get
      Future.successful(updated)
    }.recoverWith { case _ =>
      Future.failed(new IllegalArgumentException("can't update the entity, a entity with the id doesn't exist"))
    }
  }

  /** Delete a entity.
   *
   * @param id The id of the entity to delete
   * @return Future, which can fail
   */
  override def delete(id: UUID): Future[Unit] = {
    if (cache.remove(id).isDefined) {
      Future.successful(Unit)
    } else {
      Future.failed(new IllegalArgumentException("can't delete the entity, a entity with the id doesn't exist"))
    }
  }

  /** Get the id's of all entities.
   *
   * @return Future containing all id's of the entity type, can fail
   */
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
class TransientMetaModelEntityRepository
  extends TransientEntityRepository[MetaModelEntity]
    with MetaModelEntityRepository

@Singleton
class TransientMetaModelReleaseRepository
  extends TransientEntityRepository[MetaModelRelease]
    with MetaModelReleaseRepository

@Singleton
class TransientModelEntityRepository
  extends TransientEntityRepository[ModelEntity]
    with ModelEntityRepository

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
