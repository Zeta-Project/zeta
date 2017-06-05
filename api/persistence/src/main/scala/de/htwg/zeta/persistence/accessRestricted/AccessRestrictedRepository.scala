package de.htwg.zeta.persistence.accessRestricted

import java.util.UUID

import scala.concurrent.Future

import com.softwaremill.quicklens.ModifyPimp
import de.htwg.zeta.persistence.general.Repository
import de.htwg.zeta.persistence.general.Persistence
import de.htwg.zeta.persistence.general.EntityVersion
import models.User
import models.file.File


/** PersistenceService-Layer to restrict the access to the persistence.
 *
 * @param ownerId     The id of the assigned user to the restriction
 * @param underlaying The underlaying persistence Service
 */
case class AccessRestrictedRepository(ownerId: UUID, underlaying: Repository) extends Repository {

  /** Persistence for the [[models.document.EventDrivenTask]] */
  override lazy val eventDrivenTasks = AccessRestrictedPersistence(DefaultAccessHelper(this, _.accessAuthorisation.eventDrivenTasks), underlaying.eventDrivenTasks)

  /** Persistence for the [[models.document.BondedTask]] */
  override lazy val bondTasks = AccessRestrictedPersistence(DefaultAccessHelper(this, _.accessAuthorisation.bondTasks), underlaying.bondTasks)

  /** Persistence for [[models.document.TimedTask]] */
  override val timedTasks = AccessRestrictedPersistence(DefaultAccessHelper(this, _.accessAuthorisation.timedTasks), underlaying.timedTasks)

  /** Persistence for the [[models.document.Generator]] */
  override lazy val generators = AccessRestrictedPersistence(DefaultAccessHelper(this, _.accessAuthorisation.generators), underlaying.generators)

  /** Persistence for the [[models.document.Filter]] */
  override lazy val filters = AccessRestrictedPersistence(DefaultAccessHelper(this, _.accessAuthorisation.filters), underlaying.filters)

  /** Persistence for the [[models.document.GeneratorImage]] */
  override lazy val generatorImages = AccessRestrictedPersistence(DefaultAccessHelper(this, _.accessAuthorisation.generatorImages), underlaying.generatorImages)

  /** Persistence for the [[models.document.FilterImage]] */
  override lazy val filterImages = AccessRestrictedPersistence(DefaultAccessHelper(this, _.accessAuthorisation.filterImages), underlaying.filterImages)

  /** Persistence for the [[models.document.Settings]] */
  override lazy val settings = AccessRestrictedPersistence(DefaultAccessHelper(this, _.accessAuthorisation.settings), underlaying.settings)

  /** Persistence for the [[models.document.MetaModelEntity]] */
  override lazy val metaModelEntities = AccessRestrictedPersistence(DefaultAccessHelper(this, _.accessAuthorisation.metaModelEntities), underlaying.metaModelEntities)

  /** Persistence for the [[models.document.MetaModelRelease]] */
  override lazy val metaModelReleases = AccessRestrictedPersistence(DefaultAccessHelper(this, _.accessAuthorisation.metaModelReleases), underlaying.metaModelReleases)

  /** Persistence for the [[models.document.ModelEntity]] */
  override lazy val modelEntities = AccessRestrictedPersistence(DefaultAccessHelper(this, _.accessAuthorisation.modelEntities), underlaying.modelEntities)

  /** Persistence for the [[models.document.Log]] */
  override lazy val logs = AccessRestrictedPersistence(DefaultAccessHelper(this, _.accessAuthorisation.logs), underlaying.logs)

  /** Persistence for the [[models.User]] */
  override lazy val users = AccessRestrictedPersistence(UserAccessHelper(ownerId), underlaying.users)

  /** Persistence for the file indices */
  override private[persistence] val fileIndices = AccessRestrictedPersistence(DefaultAccessHelper(this, _.accessAuthorisation.fileIndices), underlaying.fileIndices)

  /** Persistence for the file versions */
  override private[persistence] val fileVersions: Persistence[EntityVersion[File]] = underlaying.fileVersions

}

/** AccessHelper */
private[accessRestricted] trait AccessHelper {

  /** Check if a id is allowed to access.
   *
   * @return Unit-Future, failed when Access is denied.
   */
  def listAccess: Future[Set[UUID]]

  /** Check if a id is allowed to access.
   *
   * @param id the id to access
   * @return Unit-Future, failed when Access is denied.
   */
  final def checkAccess(id: UUID): Future[Unit] = {
    listAccess.flatMap { ids =>
      if (ids.contains(id)) {
        Future.successful(())
      } else {
        Future.failed(new IllegalStateException("Access denied"))
      }
    }
  }

  /** Add a id to the accessible id's.
   *
   * @param id The id to add
   * @return Future
   */
  def grantAccess(id: UUID): Future[Unit]

  /** Add a id to the accessible id's.
   *
   * @param id The id to add
   * @return Future
   */
  def revokeAccess(id: UUID): Future[Unit]

}

/** The default AccessHelper
 *
 * @param repo The repo
 * @param path Path to the accessible id's
 */
private[accessRestricted] case class DefaultAccessHelper(repo: AccessRestrictedRepository, path: User => Set[UUID]) extends AccessHelper {

  /** List all accessible id's.
   *
   * @return Unit-Future
   */
  def listAccess: Future[Set[UUID]] = {
    repo.users.read(repo.ownerId).flatMap(user =>
      Future.successful(path(user.accessAuthorisation))
    )
  }

  /** Add a id to the accessible id's.
   *
   * @param id The id to add
   * @return Future
   */
  def grantAccess(id: UUID): Future[Unit] = {
    repo.users.update(repo.ownerId, _.modify(path).using(_ + UUID.randomUUID())).flatMap { _ =>
      Future.successful(())
    }
  }

  /** Add a id to the accessible id's.
   *
   * @param id The id to add
   * @return Future
   */
  def revokeAccess(id: UUID): Future[Unit] = {
    repo.users.update(repo.ownerId, _.modify(path).using(_ + UUID.randomUUID())).flatMap { _ =>
      Future.successful(())
    }
  }

}

/** User AccessHelper, allow only to access himself.
 *
 * @param userId The id of the user
 */
private[accessRestricted] case class UserAccessHelper(userId: UUID) extends AccessHelper {

  /** List all accessible id's.
   *
   * @return Unit-Future
   */
  def listAccess: Future[Set[UUID]] = {
    Future.successful(Set(userId))
  }

  /** Add a id to the accessible id's.
   *
   * @param id The id to add
   * @return Future
   */
  def grantAccess(id: UUID): Future[Unit] = {
    Future.failed(new UnsupportedOperationException("Grant Access on User not allow"))
  }

  /** Add a id to the accessible id's.
   *
   * @param id The id to add
   * @return Future
   */
  def revokeAccess(id: UUID): Future[Unit] = {
    Future.failed(new UnsupportedOperationException("Revoke Access on User not allow"))
  }

}
