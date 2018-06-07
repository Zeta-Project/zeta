package de.htwg.zeta.persistence.mongo

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.persistence.authInfo.ZetaLoginInfo
import de.htwg.zeta.persistence.general.LoginInfoRepository
import de.htwg.zeta.persistence.mongo.MongoLoginInfoRepository.UserIdReader
import de.htwg.zeta.persistence.mongo.MongoLoginInfoRepository.collectionName
import de.htwg.zeta.persistence.mongo.MongoLoginInfoRepository.keyProjection
import de.htwg.zeta.persistence.mongo.MongoLoginInfoRepository.sLoginInfo
import de.htwg.zeta.persistence.mongo.MongoLoginInfoRepository.sUserId
import javax.inject.Inject
import javax.inject.Singleton
import reactivemongo.api.Cursor
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.indexes.Index
import reactivemongo.api.indexes.IndexType
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentReader


@Singleton
class MongoLoginInfoRepository @Inject()(database: Future[DefaultDB]) extends LoginInfoRepository {

  private val formatter: ExplicitBsonPlayFormat[ZetaLoginInfo] = ExplicitBsonPlayFormat(ZetaLoginInfo)


  private val collection: Future[BSONCollection] = for {
    col <- database.map(_.collection[BSONCollection](collectionName))
    _ <- col.create().recover { case _ => }
    _ <- col.indexesManager.ensure(Index(Seq(sLoginInfo -> IndexType.Ascending), unique = true))
  } yield {
    col
  }

  /** Create a LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @param id        The id of the user.
   * @return Unit-Future, when successful.
   */
  override def create(loginInfo: ZetaLoginInfo, id: UUID): Future[Unit] = {
    implicit val format: ExplicitBsonPlayFormat[ZetaLoginInfo] = formatter
    collection.flatMap { collection =>
      collection.insert(BSONDocument(sLoginInfo -> loginInfo, sUserId -> id.toString)).flatMap(_ =>
        Future.successful(())
      )
    }
  }

  /**
   * Get a user that matches the specified LoginInfo.
   *
   * @param loginInfo The LoginInfo.
   * @return The id of the User.
   */
  override def read(loginInfo: ZetaLoginInfo): Future[UUID] = {
    implicit val reader: BSONDocumentReader[UUID] = UserIdReader
    implicit val format: ExplicitBsonPlayFormat[ZetaLoginInfo] = ExplicitBsonPlayFormat(ZetaLoginInfo)
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
  override def update(old: ZetaLoginInfo, updated: ZetaLoginInfo): Future[Unit] = {
    implicit val format: ExplicitBsonPlayFormat[ZetaLoginInfo] = formatter
    read(old).flatMap { userId =>
      collection.flatMap { collection =>
        collection.update(BSONDocument(sLoginInfo -> old), BSONDocument(sLoginInfo -> updated, sUserId -> userId.toString)).flatMap(result =>
          if (result.nModified == 1) {
            Future.successful(())
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
  override def delete(loginInfo: ZetaLoginInfo): Future[Unit] = {
    implicit val format: ExplicitBsonPlayFormat[ZetaLoginInfo] = formatter
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
  override def readAllKeys(): Future[Set[ZetaLoginInfo]] = {
    implicit val format: ExplicitBsonPlayFormat[ZetaLoginInfo] = formatter
    implicit val reader: BSONDocumentReader[Option[ZetaLoginInfo]] = BSONDocumentReader(_.getAs(sLoginInfo)(formatter))
    collection.flatMap { collection =>
      val cursor: Cursor[Option[ZetaLoginInfo]] = collection.find(BSONDocument.empty, keyProjection).cursor[Option[ZetaLoginInfo]]()
      val futOptSet = cursor.collect(-1, Cursor.FailOnError[Set[Option[ZetaLoginInfo]]]())

      futOptSet.map(_.flatten)
    }
  }

}

private object MongoLoginInfoRepository {

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
