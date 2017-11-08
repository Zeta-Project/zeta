package de.htwg.zeta.persistence.general

import java.util.UUID

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


/** Interface for the Persistence layer.
 *
 * @tparam E type of the entity
 */
trait EntityRepository[E <: Entity] { // scalastyle:ignore

  /** The name of the entity-type.
   *
   * @param m manifest
   * @return name
   */
  final def entityTypeName(implicit m: Manifest[E]): String = {
    m.runtimeClass.getSimpleName
  }

  /** Create a new entity.
   *
   * @param entity the entity to save
   * @return Future, with the created entity
   */
  def create(entity: E): Future[E]

  /** Get a single entity.
   *
   * @param id The id of the entity
   * @return Future containing the read entity
   */
  def read(id: UUID): Future[E]

  /** Update a entity.
   *
   * @param id           The id of the entity
   * @param updateEntity Function, to build the updated entity from the existing
   * @return Future containing the updated entity
   */
  def update(id: UUID, updateEntity: E => E): Future[E]

  /** Delete a entity.
   *
   * @param id The id of the entity to delete
   * @return Future
   */
  def delete(id: UUID): Future[Unit]

  /** Get the id's of all entities.
   *
   * @return Future containing all id's of the entity type
   */
  def readAllIds(): Future[Set[UUID]]

  /** Read a entity by id. If it doesn't exist create it.
   *
   * @param id     the id of the entity to read
   * @param entity the entity to create, only evaluated when needed (call-by-name)
   * @return The read or created entity
   */
  final def readOrCreate(id: UUID, entity: => E): Future[E] = {
    read(id).recoverWith {
      case _ => create(entity)
    }
  }

  /** Update a entity. If it doesn't exist create it.
   *
   * @param entity the entity to create or update
   * @return The updated or created entity
   */
  final def createOrUpdate(entity: E): Future[E] = {
    update(entity.id, _ => entity).recoverWith {
      case _ => create(entity)
    }
  }

  /** Update a entity. If it doesn't exist create it.
   *
   * @param id           the id of the entity
   * @param updateEntity function to update the existing entity
   * @param entity       the entity to create
   * @return The updated or created entity
   */
  final def createOrUpdate(id: UUID, updateEntity: E => E, entity: => E): Future[E] = {
    update(id, updateEntity).recoverWith {
      case _ => create(entity)
    }
  }

}

trait AccessAuthorisationRepository extends EntityRepository[AccessAuthorisation]

trait BondedTaskRepository extends EntityRepository[BondedTask]

trait EventDrivenTaskRepository extends EntityRepository[EventDrivenTask]

trait FilterRepository extends EntityRepository[Filter]

trait FilterImageRepository extends EntityRepository[FilterImage]

trait GeneratorRepository extends EntityRepository[Generator]

trait GeneratorImageRepository extends EntityRepository[GeneratorImage]

trait LogRepository extends EntityRepository[Log]

trait MetaModelEntityRepository extends EntityRepository[MetaModelEntity]

trait MetaModelReleaseRepository extends EntityRepository[MetaModelRelease]

trait ModelEntityRepository extends EntityRepository[ModelEntity]

trait TimedTaskRepository extends EntityRepository[TimedTask]

trait SettingsRepository extends EntityRepository[Settings]

trait UserRepository extends EntityRepository[User]
