package de.htwg.zeta.persistence.mongo

import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.mohiva.play.silhouette.api.LoginInfo
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import de.htwg.zeta.persistence.mongo.MongoLoginInfoPersistence.collectionName
import de.htwg.zeta.persistence.mongo.MongoLoginInfoPersistence.sLoginInfo
import de.htwg.zeta.persistence.mongo.MongoLoginInfoPersistence.sUserId
import de.htwg.zeta.persistence.mongo.MongoHandler.loginInfoHandler
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType
import reactivemongo.bson.BSONDocument

import scala.util.Success


class MongoLoginInfoPersistence(database: Future[DefaultDB]) extends LoginInfoPersistence {

  private val collection: Future[BSONCollection] = {
    database.map(_.collection[BSONCollection](collectionName))
  }.andThen { case Success(col) =>
    col.indexesManager.ensure(Index(Seq(sLoginInfo -> IndexType.Ascending, sUserId -> IndexType.Ascending), unique = true))
  }

  /** Create a LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @param id        The id of the user.
   * @return Unit-Future, when successful.
   */
  override def create(loginInfo: LoginInfo, id: UUID): Future[Unit] = {
    collection.flatMap { collection =>
      collection.insert(loginInfo).map(_ => BSONDocument(sLoginInfo -> loginInfo, sUserId -> id.toString))
    }
  }

  /**
   * Get a user that matches the specified LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @return The id of the User.
   */
  override def read(loginInfo: LoginInfo): Future[UUID] = {
    /*collection.flatMap { collection =>
      collection.find(BSONDocument(sId -> loginInfo)).requireOne[LoginInfo]
    }*/
    null
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
    null // TODO
  }

}

private object MongoLoginInfoPersistence {

  private val collectionName = "Login"

  private val sLoginInfo = "loginInfo"

  private val sUserId = "userId"

  private val keyProjection = BSONDocument("_id" -> 0, sLoginInfo -> 1, sUserId -> 1)

}
