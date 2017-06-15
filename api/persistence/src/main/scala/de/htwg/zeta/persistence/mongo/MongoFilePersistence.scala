package de.htwg.zeta.persistence.mongo

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.persistence.general.FilePersistence
import de.htwg.zeta.persistence.mongo.MongoFilePersistence.collectionName
import de.htwg.zeta.persistence.mongo.MongoFilePersistence.keyProjection
import de.htwg.zeta.persistence.mongo.MongoFilePersistence.sId
import de.htwg.zeta.persistence.mongo.MongoFilePersistence.sName
import de.htwg.zeta.persistence.mongo.MongoHandler.FileKey
import de.htwg.zeta.persistence.mongo.MongoHandler.IdOnlyEntity
import de.htwg.zeta.persistence.mongo.MongoHandler.fileHandler
import reactivemongo.api.Cursor
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType
import reactivemongo.bson.BSONDocument

class MongoFilePersistence(database: Future[DefaultDB]) extends FilePersistence {

  private val collection: Future[BSONCollection] = {
    database.map(_.collection[BSONCollection](collectionName))
  }.andThen { case Success(col) =>
    col.indexesManager.ensure(Index(Seq(sId -> IndexType.Ascending, sName -> IndexType.Ascending), unique = true))
  }

  /** Get the id's of all entity.
   *
   * @return Future containing all id's of the entity type
   */
  def readAllIdsOld(): Future[Set[UUID]] = {
    collection.flatMap { collection =>
      collection.find(BSONDocument.empty, keyProjection).cursor[IdOnlyEntity]().
        collect(-1, Cursor.FailOnError[Set[IdOnlyEntity]]())
    }.map(_.map(_.id))
  }

  /** Create a new file.
   *
   * @param file the file to save
   * @return Future, with the created file
   */
  override def create(file: File): Future[File] = {
    collection.flatMap { collection =>
      collection.insert(file).map(_ => file)
    }
  }

  /** Read a file.
   *
   * @param id   the id of the file
   * @param name the name of the file
   * @return Future containing the read file
   */
  override def read(id: UUID, name: String): Future[File] = {
    collection.flatMap { collection =>
      collection.find(BSONDocument(sId -> id.toString, sName -> name)).requireOne[File]
    }
  }

  /** Update a file.
   *
   * @param file The updated file
   * @return Future containing the updated file
   */
  override private[persistence] def update(file: File): Future[File] = {
    collection.flatMap { collection =>
      collection.update(BSONDocument(sId -> file.id.toString, sName -> file.name), file).flatMap(result =>
        if (result.nModified == 1) {
          Future.successful(file)
        } else {
          Future.failed(new IllegalStateException("couldn't update the file"))
        }
      )
    }
  }

  /** Delete a file.
   *
   * @param id   The id of the file to delete
   * @param name the name of the file
   * @return Future
   */
  override def delete(id: UUID, name: String): Future[Unit] = {
    collection.flatMap { collection =>
      collection.remove(BSONDocument(sId -> id.toString, sName -> name)).flatMap(result =>
        if (result.n == 1) {
          Future.successful(())
        } else {
          Future.failed(new IllegalStateException("couldn't delete the file"))
        }
      )
    }
  }

  /** Get the id's of all file.
   *
   * @return Future containing all id's of the file type
   */
  override def readAllKeys(): Future[Map[UUID, Set[String]]] = {
    collection.flatMap { collection =>
      collection.find(BSONDocument.empty, keyProjection).cursor[FileKey]().
        collect(-1, Cursor.FailOnError[Set[FileKey]]())
    }.map(x => x.groupBy(_.id).mapValues(_.map(_.name)))
  }

}

private object MongoFilePersistence {

  private val collectionName = "File"

  private val sId = "id"

  private val sName = "name"

  private val keyProjection = BSONDocument("_id" -> 0, sId -> 1, sName -> 1)

}
