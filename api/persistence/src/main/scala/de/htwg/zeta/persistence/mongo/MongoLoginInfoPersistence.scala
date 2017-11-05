package de.htwg.zeta.persistence.mongo

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

import com.mohiva.play.silhouette.api.LoginInfo
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.mongo.MongoHandler.loginInfoReader
import de.htwg.zeta.persistence.mongo.MongoHandler.loginInfoWriter
import de.htwg.zeta.persistence.mongo.MongoLoginInfoPersistence.UserIdReader
import de.htwg.zeta.persistence.mongo.MongoLoginInfoPersistence.collectionName
import de.htwg.zeta.persistence.mongo.MongoLoginInfoPersistence.keyProjection
import de.htwg.zeta.persistence.mongo.MongoLoginInfoPersistence.sLoginInfo
import de.htwg.zeta.persistence.mongo.MongoLoginInfoPersistence.sUserId
import reactivemongo.api.Cursor
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentReader


class MongoLoginInfoPersistence(database: Future[DefaultDB]) extends LoginInfoPersistence {

  private val collection: Future[BSONCollection] = {
    database.map(_.collection[BSONCollection](collectionName))
  }.andThen { case Success(col) =>
    col.create()
  }.andThen { case Success(col) =>
    col.indexesManager.ensure(Index(Seq(sLoginInfo -> IndexType.Ascending), unique = true))
  }

  /** Create a LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @param id        The id of the user.
   * @return Unit-Future, when successful.
   */
  override def create(loginInfo: LoginInfo, id: UUID): Future[Unit] = {
    collection.flatMap { collection =>
      collection.insert(BSONDocument(sLoginInfo -> loginInfo, sUserId -> id.toString)).flatMap(_ =>
        Future.successful())
    }
  }

  /**
   * Get a user that matches the specified LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @return The id of the User.
   */
  override def read(loginInfo: LoginInfo): Future[UUID] = {
    implicit val reader: BSONDocumentReader[UUID] = UserIdReader
    collection.flatMap { collection =>
      collection.find(BSONDocument(sLoginInfo -> loginInfo)).requireOne[UUID]
    }
  }

  /** Update a LoginInfo.
   *
   * @param old     The LoginInfo to update.
   * @param updated The updated LoginInfo.
   * @return Unit-Future
   */
  override def update(old: LoginInfo, updated: LoginInfo): Future[Unit] = {
    read(old).flatMap { userId =>
      collection.flatMap { collection =>
        collection.update(BSONDocument(sLoginInfo -> old), BSONDocument(sLoginInfo -> updated, sUserId -> userId.toString)).flatMap(result =>
          if (result.nModified == 1) {
            Future.successful()
          } else {
            Future.failed(new IllegalStateException("couldn't update the LoginInfo"))
          }
        )
      }
    }
  }

  /** Delete a LoginInfo.
   *
   * @param loginInfo LoginInfo
   * @return Unit-Future
   */
  override def delete(loginInfo: LoginInfo): Future[Unit] = {
    collection.flatMap { collection =>
      collection.remove(BSONDocument(sLoginInfo -> loginInfo)).flatMap(result =>
        if (result.n == 1) {
          Future.successful(())
        } else {
          Future.failed(new IllegalStateException("couldn't delete the file"))
        }
      )
    }
  }

  /** Get all LoginInfo's.
   *
   * @return Future containing all LoginInfo's
   */
  override def readAllKeys(): Future[Set[LoginInfo]] = {
    collection.flatMap { collection =>
      collection.find(BSONDocument.empty, keyProjection).cursor[LoginInfo]().
        collect(-1, Cursor.FailOnError[Set[LoginInfo]]())
    }
  }

}

private object MongoLoginInfoPersistence {

  private val collectionName = "LoginInfo"

  private val sLoginInfo = "loginInfo"

  private val sUserId = "userId"

  private val keyProjection = BSONDocument("_id" -> 0, sLoginInfo -> 1)

  private implicit object UserIdReader extends BSONDocumentReader[UUID] {

    override def read(doc: BSONDocument): UUID = {
      UUID.fromString(doc.getAs[String](sUserId).get)
    }

  }

}
