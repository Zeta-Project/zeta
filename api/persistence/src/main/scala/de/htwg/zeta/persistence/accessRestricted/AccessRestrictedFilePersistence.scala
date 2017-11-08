package de.htwg.zeta.persistence.accessRestricted

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.persistence.general.EntityRepository
import de.htwg.zeta.persistence.general.FileRepository

/** Persistence-Layer to restrict the access to the file-persistence.
 *
 * @param accessAuthorisation accessAuthorisation
 * @param underlying          The underlying Persistence
 */
@Singleton
class AccessRestrictedFilePersistence @Inject()(
    accessAuthorisation: EntityRepository[AccessAuthorisation],
    underlying: FileRepository
) {

  def restrictedTo(ownerId: UUID): FileRepository = new FileRepository {

    /** Create a new file.
     *
     * @param file the file to save
     * @return Future, with the created file
     */
    override def create(file: File): Future[File] = {
      underlying.create(file).flatMap(file =>
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
      restricted(id, name, underlying.read(id, name))
    }

    /** Update a file.
     *
     * @param file The updated file
     * @return Future containing the updated file
     */
    def update(file: File): Future[File] = {
      restricted(file.id, file.name, underlying.update(file))
    }

    /** Delete a file.
     *
     * @param id   The id of the file to delete
     * @param name the name of the file
     * @return Future
     */
    override def delete(id: UUID, name: String): Future[Unit] = {
      restricted(id, name, underlying.delete(id, name).flatMap(_ =>
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

          Future.failed(new IllegalStateException(s"Access denied: " + File.toString))
        }
      )
    }
  }


}
