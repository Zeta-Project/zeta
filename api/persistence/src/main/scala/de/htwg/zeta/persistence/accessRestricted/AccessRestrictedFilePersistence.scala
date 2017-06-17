package de.htwg.zeta.persistence.accessRestricted

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.general.FilePersistence

/** Persistence-Layer to restrict the access to the file-persistence.
 *
 * @param ownerId             the user-id of the owner
 * @param accessAuthorisation accessAuthorisation
 * @param underlaying         The underlaying Persistence
 */
class AccessRestrictedFilePersistence(
    ownerId: UUID,
    accessAuthorisation: EntityPersistence[AccessAuthorisation],
    underlaying: FilePersistence
) extends FilePersistence {

  /** Create a new file.
   *
   * @param file the file to save
   * @return Future, with the created file
   */
  override def create(file: File): Future[File] = {
    underlaying.create(file).flatMap(file =>
      accessAuthorisation.createOrUpdate(
        ownerId,
        _.grantFileAccess(file.id, file.name),
        AccessAuthorisation(ownerId, Map.empty, Map.empty).grantFileAccess(file.id, file.name)
      ).flatMap { _ =>
        Future.successful(file)
      }
    )
  }

  /** Read a file.
   *
   * @param id   the id of the file
   * @param name the name of the file
   * @return Future containing the read file
   */
  override def read(id: UUID, name: String): Future[File] = {
    restricted(id, name, underlaying.read(id, name))
  }

  /** Update a file.
   *
   * @param file The updated file
   * @return Future containing the updated file
   */
  def update(file: File): Future[File] = {
    restricted(file.id, file.name, underlaying.update(file))
  }

  /** Delete a file.
   *
   * @param id   The id of the file to delete
   * @param name the name of the file
   * @return Future
   */
  override def delete(id: UUID, name: String): Future[Unit] = {
    restricted(id, name, underlaying.delete(id, name).flatMap(_ =>
      accessAuthorisation.update(ownerId, _.revokeFileAccess(id, name)).flatMap(_ =>
        Future.successful(())
      )
    ))
  }

  /** Get the id's of all file.
   *
   * @return Future containing all id's of the file type
   */
  override def readAllKeys(): Future[Map[UUID, Set[String]]] = {
    accessAuthorisation.read(ownerId).map(_.authorizedFileAccess)
  }

  private def restricted[T](id: UUID, name: String, f: => Future[T]): Future[T] = {
    accessAuthorisation.readOrCreate(ownerId, AccessAuthorisation(ownerId, Map.empty, Map.empty)).map(
      _.checkFileAccess(id, name)).flatMap(accessGranted =>
      if (accessGranted) {
        f
      } else {
        Future.failed(new IllegalStateException("access denied"))
      }
    )
  }

}
