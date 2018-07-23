package de.htwg.zeta.persistence.accessRestricted

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe.TypeTag

import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.persistence.general.AccessAuthorisationRepository
import de.htwg.zeta.persistence.general.EntityRepository
import de.htwg.zeta.persistence.general.GdslProjectRepository
import de.htwg.zeta.persistence.general.GraphicalDslInstanceRepository
import de.htwg.zeta.persistence.general.LogRepository
import javax.inject.Inject
import javax.inject.Singleton

/** Persistence-Layer to restrict the access to the entity-persistence.
 *
 * @param accessAuthorisation accessAuthorisation
 * @param underlying          The underlying Persistence
 * @tparam E type of the entity
 * @param manifest implicit manifest of the entity type
 */
private[persistence] sealed abstract class AccessRestrictedEntityPersistence[E <: Entity: TypeTag](
    accessAuthorisation: AccessAuthorisationRepository,
    underlying: EntityRepository[E]
)(implicit manifest: Manifest[E]) {

  def restrictedTo(ownerId: UUID): EntityRepository[E] = new EntityRepository[E] {

    override def create(entity: E): Future[E] = {
      underlying.create(entity).flatMap(entity =>
        accessAuthorisation.createOrUpdate(
          ownerId,
          _.grantEntityAccess(entityTypeName, entity.id),
          AccessAuthorisation(ownerId, Map.empty, Map.empty).grantEntityAccess(entityTypeName, entity.id)
        ).flatMap { _ =>
          Future.successful(entity)
        }
      )
    }

    override def read(id: UUID): Future[E] = {
      restricted(id, underlying.read(id))
    }

    override def update(id: UUID, updateEntity: (E) => E): Future[E] = {
      restricted(id, underlying.update(id, updateEntity))
    }

    override def delete(id: UUID): Future[Unit] = {
      restricted(id, underlying.delete(id).flatMap(_ =>
        accessAuthorisation.update(ownerId, _.revokeEntityAccess(entityTypeName, id)).flatMap(_ =>
          Future.successful(())
        )
      ))
    }

    override def readAllIds(): Future[Set[UUID]] = {
      accessAuthorisation.readOrCreate(ownerId, AccessAuthorisation(ownerId, Map.empty, Map.empty)).map(_.listEntityAccess(entityTypeName))
    }

    private def restricted[T](id: UUID, f: => Future[T]): Future[T] = {
      accessAuthorisation.readOrCreate(ownerId, AccessAuthorisation(ownerId, Map.empty, Map.empty)).map(
        _.checkEntityAccess(entityTypeName, id)).flatMap(accessGranted =>
        if (accessGranted) {
          f
        } else {
          Future.failed(new IllegalStateException(s"Access denied: ${universe.typeOf[E].toString}"))
        }
      )
    }
  }

}

@Singleton
class AccessRestrictedGdslProjectRepository @Inject()(
    accessAuthorisation: AccessAuthorisationRepository,
    underlying: GdslProjectRepository
) extends AccessRestrictedEntityPersistence(accessAuthorisation, underlying)


@Singleton
class AccessRestrictedLogRepository @Inject()(
    accessAuthorisation: AccessAuthorisationRepository,
    underlying: LogRepository
) extends AccessRestrictedEntityPersistence(accessAuthorisation, underlying)
