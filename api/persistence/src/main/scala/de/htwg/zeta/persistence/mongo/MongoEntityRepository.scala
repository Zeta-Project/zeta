package de.htwg.zeta.persistence.mongo

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.common.format.entity.BondedTaskFormat
import de.htwg.zeta.common.format.entity.EventDrivenTaskFormat
import de.htwg.zeta.common.format.entity.FilterFormat
import de.htwg.zeta.common.format.entity.TimedTaskFormat
import de.htwg.zeta.common.format.metaModel.MetaModelEntityFormat
import de.htwg.zeta.common.format.metaModel.MetaModelReleaseFormat
import de.htwg.zeta.common.format.model.ModelEntityFormat
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.common.models.entity.FilterImage
import de.htwg.zeta.common.models.entity.Log
import de.htwg.zeta.common.models.entity.Settings
import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.persistence.general.AccessAuthorisationRepository
import de.htwg.zeta.persistence.general.BondedTaskRepository
import de.htwg.zeta.persistence.general.EntityRepository
import de.htwg.zeta.persistence.general.EventDrivenTaskRepository
import de.htwg.zeta.persistence.general.FilterImageRepository
import de.htwg.zeta.persistence.general.FilterRepository
import de.htwg.zeta.persistence.general.GeneratorImageRepository
import de.htwg.zeta.persistence.general.GeneratorRepository
import de.htwg.zeta.persistence.general.LogRepository
import de.htwg.zeta.persistence.general.MetaModelEntityRepository
import de.htwg.zeta.persistence.general.MetaModelReleaseRepository
import de.htwg.zeta.persistence.general.ModelEntityRepository
import de.htwg.zeta.persistence.general.SettingsRepository
import de.htwg.zeta.persistence.general.TimedTaskRepository
import de.htwg.zeta.persistence.general.UserRepository
import de.htwg.zeta.persistence.mongo.MongoEntityRepository.UuidDocumentReader
import de.htwg.zeta.persistence.mongo.MongoEntityRepository.sMongoId
import de.htwg.zeta.persistence.mongo.MongoPlayConversionHelper.readPlayJson
import de.htwg.zeta.persistence.mongo.MongoPlayConversionHelper.writePlayJson
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import reactivemongo.api.Cursor
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentHandler
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.play.json.JsObjectReader
import reactivemongo.play.json.JsObjectWriter


sealed abstract class MongoEntityRepository[E <: Entity](
    database: Future[DefaultDB],
    val entityHandler: BSONDocumentHandler[E],
    implicit val format: OFormat[E]
)(implicit manifest: Manifest[E]) extends EntityRepository[E] {

  private val collection: Future[BSONCollection] = for {
    col <- database.map(_.collection[BSONCollection](entityTypeName))
    _ <- col.create().recover { case _ => }
  } yield {
    col
  }

  /** Create a new entity.
   *
   * @param entity the entity to save
   * @return Future, with the created entity
   */
  override def create(entity: E): Future[E] = {
    collection.flatMap { collection =>
      collection.insert(writePlayJson(entity)).map(_ => entity)
    }
  }

  /** Get a single entity.
   *
   * @param id The id of the entity
   * @return Future containing the read entity
   */
  override def read(id: UUID): Future[E] = {
    collection.flatMap { collection =>
      collection.find(BSONDocument(sMongoId -> id.toString)).requireOne[JsObject].map(readPlayJson[E])
    }
  }


  /** Delete a entity.
   *
   * @param id The id of the entity to delete
   * @return Future
   */
  override def delete(id: UUID): Future[Unit] = {
    collection.flatMap { collection =>
      collection.remove(BSONDocument(sMongoId -> id.toString)).flatMap(result =>
        if (result.n == 1) {
          Future.successful(())
        } else {
          Future.failed(new IllegalStateException("couldn't delete the document"))
        }
      )
    }
  }

  /** Get the id's of all entity.
   *
   * @return Future containing all id's of the entity type
   */
  override def readAllIds(): Future[Set[UUID]] = {
    implicit val reader: BSONDocumentReader[UUID] = UuidDocumentReader
    collection.flatMap { collection =>
      collection.find(BSONDocument.empty, BSONDocument.empty).cursor[UUID]().
        collect(-1, Cursor.FailOnError[Set[UUID]]())
    }
  }

  /** Update a entity.
   *
   * @param id           The id of the entity
   * @param updateEntity Function, to build the updated entity from the existing
   * @return Future containing the updated entity
   */
  override def update(id: UUID, updateEntity: (E) => E): Future[E] = {
    read(id).flatMap { entity =>
      collection.flatMap { collection =>
        val updated = updateEntity(entity)
        collection.update(BSONDocument(sMongoId -> id.toString), writePlayJson(updated)).flatMap(result =>
          if (result.nModified == 1) {
            Future.successful(updated)
          } else {
            Future.failed(new IllegalStateException("couldn't update the document"))
          }
        )
      }
    }
  }

}

private object MongoEntityRepository {

  private val sMongoId = "_id"

  object UuidDocumentReader extends BSONDocumentReader[UUID] {

    override def read(doc: BSONDocument): UUID = {
      UUID.fromString(doc.getAs[String](sMongoId).get)
    }

  }

}

@Singleton
class MongoAccessAuthorisationRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, MongoHandler.accessAuthorisationHandler, null) // TODO use own serializer
  with AccessAuthorisationRepository

@Singleton
class MongoBondedTaskRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository[BondedTask](database, MongoHandler.bondedTaskHandler, BondedTaskFormat)
  with BondedTaskRepository

@Singleton
class MongoEventDrivenTaskRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, MongoHandler.eventDrivenTaskHandler, EventDrivenTaskFormat)
  with EventDrivenTaskRepository

@Singleton
class MongoFilterRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, MongoHandler.filterHandler, FilterFormat)
  with FilterRepository

@Singleton
class MongoFilterImageRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, MongoHandler.filterImageHandler, Json.format[FilterImage]) // TODO use own serializer
  with FilterImageRepository

@Singleton
class MongoGeneratorRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, MongoHandler.generatorHandler, null) // TODO use own serializer
  with GeneratorRepository

@Singleton
class MongoGeneratorImageRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, MongoHandler.generatorImageHandler, null)
  with GeneratorImageRepository

@Singleton
class MongoLogRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, MongoHandler.logHandler, Json.format[Log]) // TODO use own serializer
  with LogRepository

@Singleton
class MongoMetaModelEntityRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, MongoHandler.metaModelEntityHandler, MetaModelEntityFormat)
  with MetaModelEntityRepository

@Singleton
class MongoMetaModelReleaseRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, MongoHandler.metaModelReleaseHandler, MetaModelReleaseFormat)
  with MetaModelReleaseRepository

@Singleton
class MongoModelEntityRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, MongoHandler.modelEntityHandler, ModelEntityFormat)
  with ModelEntityRepository

@Singleton
class MongoSettingsRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, MongoHandler.settingsHandler, Json.format[Settings]) // TODO use own serializer
  with SettingsRepository

@Singleton
class MongoTimedTaskRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, MongoHandler.timedTaskHandler, TimedTaskFormat)
  with TimedTaskRepository

@Singleton
class MongoUserRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, MongoHandler.userHandler, Json.format[User])
  with UserRepository
