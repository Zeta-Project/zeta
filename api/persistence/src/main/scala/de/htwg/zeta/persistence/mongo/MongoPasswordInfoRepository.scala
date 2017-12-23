package de.htwg.zeta.persistence.mongo

import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.persistence.authInfo.ZetaLoginInfo
import de.htwg.zeta.persistence.authInfo.ZetaPasswordInfo
import de.htwg.zeta.persistence.general.PasswordInfoRepository
import de.htwg.zeta.persistence.mongo.MongoPasswordInfoRepository.collectionName
import de.htwg.zeta.persistence.mongo.MongoPasswordInfoRepository.loginInfoProjection
import de.htwg.zeta.persistence.mongo.MongoPasswordInfoRepository.sLoginInfo
import de.htwg.zeta.persistence.mongo.MongoPasswordInfoRepository.sPasswordInfo
import reactivemongo.api.Cursor
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType
import reactivemongo.bson.BSONDocument

@Singleton
class MongoPasswordInfoRepository @Inject()(database: Future[DefaultDB]) extends PasswordInfoRepository {

  private val collection: Future[BSONCollection] = for {
    col <- database.map(_.collection[BSONCollection](collectionName))
    _ <- col.create().recover { case _ => }
    _ <- col.indexesManager.ensure(Index(Seq(sLoginInfo -> IndexType.Ascending), unique = true))
  } yield {
    col
  }

  /** Finds the auth info which is linked to the specified login info.
   *
   * @param loginInfo The linked login info.
   * @return The found auth info or None if no auth info could be found for the given login info.
   */
  override def find(loginInfo: ZetaLoginInfo): Future[Option[ZetaPasswordInfo]] = {
    collection.flatMap { collection =>
      implicit val loginFormat: ExplicitBsonPlayFormat[ZetaLoginInfo] = ExplicitBsonPlayFormat(ZetaLoginInfo)
      implicit val passwordFormat: ExplicitBsonPlayFormat[ZetaPasswordInfo] = ExplicitBsonPlayFormat(ZetaPasswordInfo)
      collection.find(BSONDocument(sLoginInfo -> loginInfo)).one[ZetaPasswordInfo]
    }
  }

  /** Adds new auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be added.
   * @param authInfo  The auth info to add.
   * @return The added auth info.
   */
  override def add(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Future[ZetaPasswordInfo] = {
    collection.flatMap { collection =>
      implicit val loginFormat: ExplicitBsonPlayFormat[ZetaLoginInfo] = ExplicitBsonPlayFormat(ZetaLoginInfo)
      implicit val passwordFormat: ExplicitBsonPlayFormat[ZetaPasswordInfo] = ExplicitBsonPlayFormat(ZetaPasswordInfo)
      collection.insert(BSONDocument(sLoginInfo -> loginInfo, sPasswordInfo -> authInfo)).flatMap(_ =>
        Future.successful(authInfo))
    }
  }

  /** Updates the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be updated.
   * @param authInfo  The auth info to update.
   * @return The updated auth info.
   */
  override def update(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Future[ZetaPasswordInfo] = {
    collection.flatMap { collection =>
      implicit val loginFormat: ExplicitBsonPlayFormat[ZetaLoginInfo] = ExplicitBsonPlayFormat(ZetaLoginInfo)
      implicit val passwordFormat: ExplicitBsonPlayFormat[ZetaPasswordInfo] = ExplicitBsonPlayFormat(ZetaPasswordInfo)
      collection.update(BSONDocument(sLoginInfo -> loginInfo), BSONDocument(sLoginInfo -> loginInfo, sPasswordInfo -> authInfo)).flatMap(result =>
        if (result.nModified == 1) {
          Future.successful(authInfo)
        } else {
          Future.failed(new IllegalStateException("couldn't update the LoginInfo"))
        }
      )
    }
  }

  /** Saves the auth info for the given login info. This method either adds the auth info if it doesn't exists or it updates the auth info if it already exists.
   *
   * @param loginInfo The login info for which the auth info should be saved.
   * @param authInfo  The auth info to save.
   * @return The saved auth info.
   */
  override def save(loginInfo: ZetaLoginInfo, authInfo: ZetaPasswordInfo): Future[ZetaPasswordInfo] = {
    add(loginInfo, authInfo).recoverWith { case _ =>
      update(loginInfo, authInfo) }
  }

  /** Removes the auth info for the given login info.
   *
   * @param loginInfo The login info for which the auth info should be removed.
   * @return A future to wait for the process to be completed.
   */
  override def remove(loginInfo: ZetaLoginInfo): Future[Unit] = {
    collection.flatMap { collection =>
      implicit val loginFormat: ExplicitBsonPlayFormat[ZetaLoginInfo] = ExplicitBsonPlayFormat(ZetaLoginInfo)
      collection.remove(BSONDocument(sLoginInfo -> loginInfo)).flatMap(result =>
        if (result.n == 1) {
          Future.successful(())
        } else {
          Future.failed(new IllegalStateException("couldn't delete the LoginInfo"))
        }
      )
    }
  }

  /** Read all LoginInfo's
   *
   * @return all LoginInfo's
   */
  override def readAllKeys(): Future[Set[ZetaLoginInfo]] = {
    collection.flatMap { collection =>
      implicit val loginFormat: ExplicitBsonPlayFormat[ZetaLoginInfo] = ExplicitBsonPlayFormat(ZetaLoginInfo)
      collection.find(BSONDocument.empty, loginInfoProjection).cursor[ZetaLoginInfo]().
        collect(-1, Cursor.FailOnError[Set[ZetaLoginInfo]]())
    }
  }

}


private object MongoPasswordInfoRepository {

  private val collectionName = "PasswordInfo"
  private val sLoginInfo = "loginInfo"
  private val sPasswordInfo = "passwordInfo"

  private val loginInfoProjection = BSONDocument("_id" -> 0, sLoginInfo -> 1)

}
