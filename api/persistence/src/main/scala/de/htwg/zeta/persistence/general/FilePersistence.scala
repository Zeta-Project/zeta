package de.htwg.zeta.persistence.general

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import models.file.File

/** Modification of the EntityPersistence. The key is a compound key and consists of the id and the name of the file. */
trait FilePersistence {

  /** Create a new file.
   *
   * @param file the file to save
   * @return Future, with the created file
   */
  def create(file: File): Future[File]

  /** Read a file.
   *
   * @param id   the id of the file
   * @param name the name of the file
   * @return Future containing the read file
   */
  def read(id: UUID, name: String): Future[File]

  /** Update a file.
   *
   * @param id         The id of the file
   * @param name       the name of the file
   * @param updateFile Function, to build the updated file from the existing
   * @return Future containing the updated file
   */
  final def update(id: UUID, name: String, updateFile: File => File): Future[File] = {
    read(id, name).flatMap(file => update(updateFile(file)))
  }

  /** Update a file.
   *
   * @param file The updated file
   * @return Future containing the updated file
   */
  private[persistence] def update(file: File): Future[File]

  /** Delete a file.
   *
   * @param id   The id of the file to delete
   * @param name the name of the file
   * @return Future
   */
  def delete(id: UUID, name: String): Future[Unit]

  /** Get the id's of all file.
   *
   * @return Future containing all id's of the file type
   */
  def readAllIds(): Future[Map[UUID, Set[String]]]

  /** Read a file by id. If it doesn't exist create it.
   *
   * @param id   the id of the file to read
   * @param name the name of the file
   * @param file the file to create, only evaluated when needed (call-by-name)
   * @return The read or created file
   */
  final def readOrCreate(id: UUID, name: String, file: => File): Future[File] = {
    read(id, name).recoverWith {
      case _ => create(file)
    }
  }

  /** Update a file. If it doesn't exist create it.
   *
   * @param id         the id of the file to read
   * @param name       the name of the file
   * @param updateFile Function, to build the updated file from the existing
   * @param file       the file to create, only evaluated when needed (call-by-name)
   * @return The updated or created file
   */
  final def updateOrCreate(id: UUID, name: String, updateFile: File => File, file: => File): Future[File] = {
    update(id, name, updateFile).recoverWith {
      case _ => create(file)
    }
  }

}
