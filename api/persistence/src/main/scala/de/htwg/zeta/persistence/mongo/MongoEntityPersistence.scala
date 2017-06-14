package de.htwg.zeta.persistence.mongo

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

import de.htwg.zeta.persistence.general.EntityPersistence
import de.htwg.zeta.persistence.mongo.MongoEntityPersistence.idProjection
import de.htwg.zeta.persistence.mongo.MongoEntityPersistence.sId
import de.htwg.zeta.persistence.mongo.MongoHandler.IdOnlyEntity
import models.entity.Entity
import reactivemongo.api.Cursor
import reactivemongo.api.DefaultDB
import reactivemongo.api.MongoConnection
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

  /** Update a entity.
   *
   * @param entity The updated entity
   * @return Future containing the updated entity
   */
  override private[persistence] def update(entity: E): Future[E] = {
    collection.flatMap { collection =>
      collection.update(BSONDocument(sId -> entity.id.toString), entity).flatMap(result =>
        if (result.nModified == 1) {
          Future.successful(entity)
        } else {
          Future.failed(new IllegalStateException("couldn't update the document"))
        }
      )
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
    collection.flatMap { collection =>
      collection.find(BSONDocument.empty, idProjection).cursor[IdOnlyEntity]().
        collect(-1, Cursor.FailOnError[Set[IdOnlyEntity]]())
    }.map(_.map(_.id))
  }

}

private object MongoEntityPersistence {

  private val sId = "id"

  private val idProjection = BSONDocument("_id" -> 0, sId -> 1)

}
