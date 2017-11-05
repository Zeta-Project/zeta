package de.htwg.zeta.persistence.mongo

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.mongo.MongoEntityPersistence.UuidDocumentReader
import de.htwg.zeta.persistence.mongo.MongoEntityPersistence.idProjection
import de.htwg.zeta.persistence.mongo.MongoEntityPersistence.sId
import reactivemongo.api.Cursor
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter

class MongoEntityPersistence[E <: Entity](
    database: Future[DefaultDB],
    implicit val entityHandler: BSONDocumentWriter[E] with BSONDocumentReader[E])(
    implicit manifest: Manifest[E])
  extends EntityPersistence[E] {

  private val collection: Future[BSONCollection] = {
    database.map(_.collection[BSONCollection](entityTypeName))
  }.andThen { case Success(col) =>
    col.create()
  }.andThen { case Success(col) =>
    col.indexesManager.ensure(Index(Seq(sId -> IndexType.Ascending), unique = true))
  }

  /** Create a new entity.
   *
   * @param entity the entity to save
   * @return Future, with the created entity
   */
  override def create(entity: E): Future[E] = {
    collection.flatMap { collection =>
      collection.insert(entity).map(_ => entity)
    }
  }

  /** Get a single entity.
   *
   * @param id The id of the entity
   * @return Future containing the read entity
   */
  override def read(id: UUID): Future[E] = {
    collection.flatMap { collection =>
      collection.find(BSONDocument(sId -> id.toString)).requireOne[E]
    }
  }


  /** Delete a entity.
   *
   * @param id The id of the entity to delete
   * @return Future
   */
  override def delete(id: UUID): Future[Unit] = {
    collection.flatMap { collection =>
      collection.remove(BSONDocument(sId -> id.toString)).flatMap(result =>
        if (result.n == 1) {
          Future.successful(())
        } else {
          Future.failed(new IllegalStateException("couldn't delete the document"))
        }
      )
    }
  }

  /** Get the id's of all entity.
   *
   * @return Future containing all id's of the entity type
   */
  override def readAllIds(): Future[Set[UUID]] = {
    implicit val reader: BSONDocumentReader[UUID] = UuidDocumentReader
    collection.flatMap { collection =>
      collection.find(BSONDocument.empty, idProjection).cursor[UUID]().
        collect(-1, Cursor.FailOnError[Set[UUID]]())
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
      collection.flatMap { collection =>
        val updated = updateEntity(entity)
        collection.update(BSONDocument(sId -> id.toString), updated).flatMap(result =>
          if (result.nModified == 1) {
            Future.successful(updated)
          } else {
            Future.failed(new IllegalStateException("couldn't update the document"))
          }
        )
      }
    }
  }

}

private object MongoEntityPersistence {

  private val sId = "id"

  private val idProjection = BSONDocument("_id" -> 0, sId -> 1)

  object UuidDocumentReader extends BSONDocumentReader[UUID] {

    override def read(doc: BSONDocument): UUID = {
      UUID.fromString(doc.getAs[String](sId).get)
    }

  }

}
