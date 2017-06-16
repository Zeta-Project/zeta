package de.htwg.zeta.persistence.transient

import java.util.UUID

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.persistence.general.FilePersistence

/**
 * Transient Implementation of FilePersistence.
 */
class TransientFilePersistence extends FilePersistence {

  private case class Key(id: UUID, name: String)

  private val cache: TrieMap[Key, File] = TrieMap.empty

  /** Create a new file.
   *
   * @param file the file to save
   * @return Future, with the created file
   */
  override def create(file: File): Future[File] = {
    if (cache.putIfAbsent(Key(file.id, file.name), file).isEmpty) {
      Future.successful(file)
    } else {
      Future.failed(new IllegalArgumentException("cant't create the file, a file with same id and name already exists"))
    }
  }

  /** Read a file.
   *
   * @param id   the id of the file
   * @param name the name of the file
   * @return Future containing the read file
   */
  override def read(id: UUID, name: String): Future[File] = {
    cache.get(Key(id, name)).fold[Future[File]] {
      Future.failed(new IllegalArgumentException("can't read the file, a entity with the id and name doesn't exist"))
    } {
      Future.successful
    }
  }

  /** Update a file.
   *
   * @param file The updated file
   * @return Future containing the updated file
   */
  override private[persistence] def update(file: File): Future[File] = {
    if (cache.replace(Key(file.id, file.name), file).isDefined) {
      Future.successful(file)
    } else {
      Future.failed(new IllegalArgumentException("can't update the file, a file with the id and name doesn't exist"))
    }
  }

  /** Delete a file.
   *
   * @param id   The id of the file to delete
   * @param name the name of the file
   * @return Future
   */
  override def delete(id: UUID, name: String): Future[Unit] = {
    if (cache.remove(Key(id, name)).isDefined) {
      Future.successful(Unit)
    } else {
      Future.failed(new IllegalArgumentException("can't delete the file, a file with the id and name doesn't exist"))
    }
  }

  /** Get the id's of all file.
   *
   * @return Future containing all id's of the file type
   */
  override def readAllKeys(): Future[Map[UUID, Set[String]]] = {
    Future.successful(cache.keys.groupBy(_.id).mapValues(_.map(_.name).toSet))
  }

}
