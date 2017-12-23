package de.htwg.zeta.persistence.mongo

import java.util.UUID

import de.htwg.zeta.common.models.entity.File
import reactivemongo.bson.BSONDocumentHandler
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

}
