package de.htwg.zeta.persistence



import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.persistence.general.Persistence
import models.Entity
import reactivemongo.api.Cursor
import reactivemongo.api.MongoConnection
import reactivemongo.api.MongoDriver
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentHandler
import de.htwg.zeta.persistence.mongo.MongoHandler.UserHandler
import models.User


object MongoTest extends App {






  private val sId = "_id"

  private val connection: Future[MongoConnection] = Future.fromTry {
    MongoDriver().connection("127.0.0.1:27017")
  }

  private val idProjection = BSONDocument(sId -> 1)

  private def doDatabaseAction[T](f: BSONCollection => Future[T]): Future[T] = {
    connection.flatMap(_.database("zeta")).map(_.collection("users")).flatMap(f)
  }



  val user = User(UUID.randomUUID, "first", "last", "fist@last.com", activated = true)

  doDatabaseAction { collection =>
    collection.insert(user)
  }

  Thread.sleep(3000)


  /** Create a new entity.
   *
   * @param entity the entity to save
   * @return Future, with the created entity
   *
  override def create(entity: E): Future[E] = {
    doDatabaseAction { collection =>
      collection.insert(entity).map(_ => entity)
    }
  }*/

  /** Get a single entity.
   *
   * @param id The id of the entity
   * @return Future containing the read entity
   *
  override def read(id: UUID): Future[E] = {
    doDatabaseAction { collection =>
      // implicit val idWriter = idHandler
      // implicit val entityReader = entityHandler
      collection.find(BSONDocument(sId -> id)).requireOne[E]
    }
  } */

  /** Update a entity.
   *
   * @param entity The updated entity
   * @return Future containing the updated entity
   *
  override private[persistence] def update(entity: E): Future[E] = {
    doDatabaseAction { collection =>
      collection.findAndUpdate(BSONDocument(sId -> entity), entity).map(_ =>
        entity
      )
    }
  } */

  /** Delete a entity.
   *
   * @param id The id of the entity to delete
   * @return Future
   *
  override def delete(id: UUID): Future[Unit] = {
    doDatabaseAction { collection =>
      collection.remove(BSONDocument(sId -> id)).flatMap(_ =>
        Future.successful(())
      )
    }
  } */

  /** Get the id's of all entity.
   *
   * @return Future containing all id's of the entity type
   *
  override def readAllIds(): Future[Set[UUID]] = {
    doDatabaseAction { collection =>
      collection.find(BSONDocument.empty, idProjection).cursor[UUID]().
        collect(-1, Cursor.FailOnError[Set[UUID]]())
    }
  } */

}
