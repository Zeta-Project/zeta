package de.htwg.zeta.persistence.mongo

import java.util.UUID

import de.htwg.zeta.persistence.accessRestricted.AccessAuthorisation
import models.User
import models.Entity
import models.document.EventDrivenTask
import models.document.BondedTask
import models.document.TimedTask
import models.document.Generator
import models.document.Filter
import models.document.GeneratorImage
import models.document.FilterImage
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.Macros
import reactivemongo.bson.BSONDocumentHandler
import reactivemongo.bson.BSONString
import reactivemongo.bson.BSONReader
import reactivemongo.bson.BSONWriter
import reactivemongo.bson.BSONBoolean


object MongoHandler {

  private val sId = "_id"

  implicit object IdHandler extends BSONReader[BSONString, UUID] with BSONWriter[UUID, BSONString] {

    def read(doc: BSONString): UUID = {
      UUID.fromString(doc.value)
    }

    def write(id: UUID): BSONString = {
      BSONString(id.toString)
    }

  }


  implicit val reader: BSONDocumentHandler[IdOnlyEntity] = Macros.handler[IdOnlyEntity]
  case class IdOnlyEntity(id: UUID) extends Entity


  implicit val mapStringSetIdHandler = new BSONDocumentReader[Map[String, Set[UUID]]] with BSONDocumentWriter[Map[String, Set[UUID]]] {

    override def read(doc: BSONDocument): Map[String, Set[UUID]] = {

      null // TODO

    }

    override def write(t: Map[String, Set[UUID]]): BSONDocument = {
      null // TODO

    }

  }


  //  case class IdOnlyEntity(id: UUID) extends Entity

  //  implicit val idOnlyEntity: BSONDocumentHandler[IdOnlyEntity] = Macros.handler[IdOnlyEntity]

  implicit val accessAuthorisationHandler: BSONDocumentHandler[AccessAuthorisation] = Macros.handler[AccessAuthorisation]

  implicit val eventDrivenTaskHandler: BSONDocumentHandler[EventDrivenTask] = Macros.handler[EventDrivenTask]

  implicit val bondedTaskHandler: BSONDocumentHandler[BondedTask] = Macros.handler[BondedTask]

  implicit val timedTaskHandler: BSONDocumentHandler[TimedTask] = Macros.handler[TimedTask]

  implicit val generatorHandler: BSONDocumentHandler[Generator] = Macros.handler[Generator]

  implicit val filterHandler: BSONDocumentHandler[Filter] = Macros.handler[Filter]

  implicit val generatorImageHandler: BSONDocumentHandler[GeneratorImage] = Macros.handler[GeneratorImage]

  implicit val filterImageHandler: BSONDocumentHandler[FilterImage] = Macros.handler[FilterImage]

  // implicit val settingsHandler: BSONDocumentHandler[Settings] = Macros.handler[Settings]

  // implicit val metaModelEntityHandler: BSONDocumentHandler[MetaModelEntity] = Macros.handler[MetaModelEntity]

  // implicit val versionIndexIntHandler: BSONDocumentHandler[VersionIndex[Int]] = Macros.handler[VersionIndex[Int]]

  // implicit val entityVersionMetaModelReleaseHandler: BSONDocumentHandler[EntityVersion[MetaModelRelease]] = Macros.handler[EntityVersion[MetaModelRelease]]

  // implicit val modelEntityHandler: BSONDocumentHandler[ModelEntity] = Macros.handler[ModelEntity]

  // implicit val logHandler: BSONDocumentHandler[Log] = Macros.handler[Log]

  // implicit val userHandler: BSONDocumentHandler[User] = Macros.handler[User]
  implicit object UserHandler extends BSONDocumentWriter[User] with BSONDocumentReader[User] {

    private val sFirstName = "firstName"
    private val sLastName = "lastName"
    private val sEmail = "email"
    private val sActivated = "activated"

    override def write(user: User): BSONDocument = {
      BSONDocument(
        sId -> IdHandler.write(user.id),
        sFirstName -> BSONString(user.firstName),
        sLastName -> BSONString(user.lastName),
        sEmail -> BSONString(user.email),
        sActivated -> BSONBoolean(user.activated)
      )
    }

    override def read(doc: BSONDocument): User = {
      User(
        id = doc.getAs[UUID](sId).get,
        firstName = doc.getAs[String](sFirstName).get,
        lastName = doc.getAs[String](sLastName).get,
        email = doc.getAs[String](sEmail).get,
        activated = doc.getAs[Boolean](sActivated).get
      )
    }

  }


  // implicit val versionIndexStringHandler: BSONDocumentHandler[VersionIndex[String]] = Macros.handler[VersionIndex[String]]

  // implicit val entityVersionFileHandler: BSONDocumentHandler[EntityVersion[File]] = Macros.handler[EntityVersion[File]]

}
