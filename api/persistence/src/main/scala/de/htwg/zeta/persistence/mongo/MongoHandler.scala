package de.htwg.zeta.persistence.mongo

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import de.htwg.zeta.common.models.entity.File
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentHandler
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONHandler
import reactivemongo.bson.BSONString
import reactivemongo.bson.Macros

@SuppressWarnings(Array("org.wartremover.warts.OptionPartial", "org.wartremover.warts.Var", "org.wartremover.warts.TryPartial", "org.wartremover.warts.Throw"))
object MongoHandler {

  implicit object UuidHandler extends BSONHandler[BSONString, UUID] {

    def read(doc: BSONString): UUID = {
      UUID.fromString(doc.value)
    }

    def write(id: UUID): BSONString = {
      BSONString(id.toString)
    }

  }

  case class FileKey(id: UUID, name: String)

  implicit val fileKeyHandler: BSONDocumentHandler[FileKey] = Macros.handler[FileKey]

  implicit val fileHandler: BSONDocumentHandler[File] = Macros.handler[File]

  implicit val loginInfoWriter: BSONDocumentWriter[LoginInfo] = Macros.writer[LoginInfo]

  implicit val loginInfoReader: BSONDocumentReader[LoginInfo] = new BSONDocumentReader[LoginInfo] {
    override def read(doc: BSONDocument): LoginInfo = {
      val subDoc = doc.getAs[BSONDocument]("loginInfo").getOrElse(doc)
      LoginInfo(
        providerID = subDoc.getAs[String]("providerID").getOrElse(
          throw new IllegalArgumentException("Reading LoginInfo from MongoDB failed, missing field providerID")
        ),
        providerKey = subDoc.getAs[String]("providerKey").getOrElse(
          throw new IllegalArgumentException("Reading LoginInfo from MongoDB failed, missing field providerKey")
        )
      )
    }
  }

  implicit val passwordInfoWriter: BSONDocumentWriter[PasswordInfo] = Macros.writer[PasswordInfo]

  implicit val passwordInfoReader: BSONDocumentReader[PasswordInfo] = new BSONDocumentReader[PasswordInfo] {
    override def read(doc: BSONDocument): PasswordInfo = {

      val subDoc = doc.getAs[BSONDocument]("passwordInfo").getOrElse(
        throw new IllegalArgumentException("Reading PasswordInfo from MongoDB failed, missing field passwordInfo")
      )

      PasswordInfo(
        hasher = subDoc.getAs[String]("hasher").getOrElse(
          throw new IllegalArgumentException("Reading PasswordInfo from MongoDB failed, missing field hasher")
        ),
        password = subDoc.getAs[String]("password").getOrElse(
          throw new IllegalArgumentException("Reading PasswordInfo from MongoDB failed, missing field password")
        ),
        salt = subDoc.getAs[String]("salt")
      )
    }
  }

}
