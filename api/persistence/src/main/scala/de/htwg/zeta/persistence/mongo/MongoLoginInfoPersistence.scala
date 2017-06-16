package de.htwg.zeta.persistence.mongo

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

import com.mohiva.play.silhouette.api.LoginInfo
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.mongo.MongoHandler.UserIdOnlyEntity
import de.htwg.zeta.persistence.mongo.MongoHandler.loginInfoHandler
import de.htwg.zeta.persistence.mongo.MongoHandler.userIdOnlyEntityHandler
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


class MongoLoginInfoPersistence(database: Future[DefaultDB]) extends LoginInfoPersistence {

  private val collection: Future[BSONCollection] = {
    database.map(_.collection[BSONCollection](collectionName))
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
    collection.flatMap { collection =>
      collection.find(BSONDocument(sLoginInfo -> loginInfo)).requireOne[UserIdOnlyEntity].map(_.userId)
    }
  }

  /** Update a LoginInfo.
   *
   * @param old     The LoginInfo to update.
   * @param updated The updated LoginInfo.
   * @return Unit-Future
   */
  override def update(old: LoginInfo, updated: LoginInfo): Future[Unit] = {
    null // TODO
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

  private val keyProjection = BSONDocument("_id" -> 0, sLoginInfo -> 1, sUserId -> 1)

}
