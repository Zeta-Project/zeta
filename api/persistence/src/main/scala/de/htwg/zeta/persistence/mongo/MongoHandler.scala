package de.htwg.zeta.persistence.mongo

import java.util.UUID

import scala.util.Success

import de.htwg.zeta.persistence.accessRestricted.AccessAuthorisation
import de.htwg.zeta.persistence.general.VersionIndex
import de.htwg.zeta.persistence.general.EntityVersion
import models.User
import models.document.EventDrivenTask
import models.document.BondedTask
import models.document.TimedTask
import models.document.Generator
import models.document.Filter
import models.document.GeneratorImage
import models.document.FilterImage
import models.document.Settings
import models.document.MetaModelRelease
import models.document.ModelEntity
import models.document.Log
import models.document.MetaModelEntity
import models.file.File
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.Macros
import reactivemongo.bson.BSONDocumentHandler
import reactivemongo.bson.BSONObjectID


object MongoHandler {

  private val sId = "_id"

  implicit val idHandler = new BSONDocumentReader[UUID] with BSONDocumentWriter[UUID] {

    override def read(doc: BSONDocument): UUID = {
      UUID.fromString(doc.getAs[BSONObjectID](sId).get.stringify)
    }

    override def write(id: UUID): BSONDocument = {
      BSONObjectID.parse {
        case Success(d) => BSONDocument(sId -> d.toString)
      }


    }

  }


  implicit val mapStringSetIdHandler = new BSONDocumentReader[Map[String, Set[UUID]]] with BSONDocumentWriter[Map[String, Set[UUID]]] {

    override def read(doc: BSONDocument): Map[String, Set[UUID]] = {

      null // TODO

    }

    override def write(t: Map[String, Set[UUID]]): BSONDocument = {
      null // TODO

    }

  }

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

  implicit val userHandler: BSONDocumentHandler[User] = Macros.handler[User]

  // implicit val versionIndexStringHandler: BSONDocumentHandler[VersionIndex[String]] = Macros.handler[VersionIndex[String]]

  // implicit val entityVersionFileHandler: BSONDocumentHandler[EntityVersion[File]] = Macros.handler[EntityVersion[File]]

}
