package de.htwg.zeta.persistence.mongo

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.common.format.entity.AccessAuthorisationFormat
import de.htwg.zeta.common.format.entity.BondedTaskFormat
import de.htwg.zeta.common.format.entity.EventDrivenTaskFormat
import de.htwg.zeta.common.format.entity.FilterFormat
import de.htwg.zeta.common.format.entity.FilterImageFormat
import de.htwg.zeta.common.format.entity.GeneratorFormat
import de.htwg.zeta.common.format.entity.GeneratorImageFormat
import de.htwg.zeta.common.format.entity.LogFormat
import de.htwg.zeta.common.format.entity.SettingsFormat
import de.htwg.zeta.common.format.entity.TimedTaskFormat
import de.htwg.zeta.common.format.entity.UserFormat
import de.htwg.zeta.common.format.metaModel.AttributeFormat
import de.htwg.zeta.common.format.metaModel.AttributeTypeFormat
import de.htwg.zeta.common.format.metaModel.AttributeValueFormat
import de.htwg.zeta.common.format.metaModel.ClassFormat
import de.htwg.zeta.common.format.metaModel.DslFormat
import de.htwg.zeta.common.format.metaModel.EnumFormat
import de.htwg.zeta.common.format.metaModel.MetaModelEntityFormat
import de.htwg.zeta.common.format.metaModel.MetaModelFormat
import de.htwg.zeta.common.format.metaModel.MetaModelReleaseFormat
import de.htwg.zeta.common.format.metaModel.MethodFormat
import de.htwg.zeta.common.format.metaModel.ReferenceFormat
import de.htwg.zeta.common.format.model.EdgeFormat
import de.htwg.zeta.common.format.model.ModelEntityFormat
import de.htwg.zeta.common.format.model.ModelFormat
import de.htwg.zeta.common.format.model.NodeFormat
import de.htwg.zeta.common.models.entity.Entity
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
import de.htwg.zeta.persistence.mongo.MongoEntityRepository.dslFormat
import de.htwg.zeta.persistence.mongo.MongoEntityRepository.metaModelFormat
import de.htwg.zeta.persistence.mongo.MongoEntityRepository.modelFormat
import de.htwg.zeta.persistence.mongo.MongoEntityRepository.sMongoId
import de.htwg.zeta.persistence.mongo.MongoPlayConversionHelper.readPlayJson
import de.htwg.zeta.persistence.mongo.MongoPlayConversionHelper.writePlayJson
import play.api.libs.json.JsObject
import play.api.libs.json.OFormat
import reactivemongo.api.Cursor
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.play.json.JsObjectReader
import reactivemongo.play.json.JsObjectWriter


sealed abstract class MongoEntityRepository[E <: Entity](
    database: Future[DefaultDB],
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

private[mongo] object MongoEntityRepository {

  val sMongoId = "_id"

  object UuidDocumentReader extends BSONDocumentReader[UUID] {
    override def read(doc: BSONDocument): UUID = {
      UUID.fromString(doc.getAs[String](sMongoId).getOrElse(
        throw new IllegalArgumentException("Can't read _id from MongoDB and convert it to UUID")
      ))
    }
  }

  val attributeTypeFormat = new AttributeTypeFormat
  val attributeValueFormat = new AttributeValueFormat
  val attributeFormat = new AttributeFormat(attributeTypeFormat, attributeValueFormat)
  val methodFormat = new MethodFormat(attributeTypeFormat)
  val enumFormat = new EnumFormat

  val classFormat = new ClassFormat(attributeFormat, methodFormat)
  val referenceFormat = new ReferenceFormat(attributeFormat, methodFormat)
  val metaModelFormat = new MetaModelFormat(enumFormat, classFormat, referenceFormat, attributeFormat, methodFormat)
  val dslFormat = new DslFormat

  val nodeFormat = new NodeFormat(attributeFormat, attributeValueFormat, methodFormat)
  val edgeFormat = new EdgeFormat(attributeFormat, attributeValueFormat, methodFormat)
  val modelFormat = new ModelFormat(nodeFormat, edgeFormat, attributeFormat, attributeValueFormat, methodFormat)

}

@Singleton
class MongoAccessAuthorisationRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new AccessAuthorisationFormat(sId = sMongoId))
  with AccessAuthorisationRepository

@Singleton
class MongoBondedTaskRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new BondedTaskFormat(sId = sMongoId))
  with BondedTaskRepository

@Singleton
class MongoEventDrivenTaskRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new EventDrivenTaskFormat(sId = sMongoId))
  with EventDrivenTaskRepository

@Singleton
class MongoFilterRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new FilterFormat(sId = sMongoId))
  with FilterRepository

@Singleton
class MongoFilterImageRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new FilterImageFormat(sId = sMongoId))
  with FilterImageRepository

@Singleton
class MongoGeneratorRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new GeneratorFormat(sId = sMongoId))
  with GeneratorRepository

@Singleton
class MongoGeneratorImageRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new GeneratorImageFormat(sId = sMongoId))
  with GeneratorImageRepository

@Singleton
class MongoLogRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new LogFormat(sId = sMongoId))
  with LogRepository

@Singleton
class MongoMetaModelEntityRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new MetaModelEntityFormat(metaModelFormat, dslFormat, sId = sMongoId))
  with MetaModelEntityRepository

@Singleton
class MongoMetaModelReleaseRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new MetaModelReleaseFormat(metaModelFormat, dslFormat, sId = sMongoId))
  with MetaModelReleaseRepository

@Singleton
class MongoModelEntityRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new ModelEntityFormat(modelFormat, sId = sMongoId))
  with ModelEntityRepository

@Singleton
class MongoSettingsRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new SettingsFormat(sId = sMongoId))
  with SettingsRepository

@Singleton
class MongoTimedTaskRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new TimedTaskFormat(sId = sMongoId))
  with TimedTaskRepository

@Singleton
class MongoUserRepository @Inject()(
    database: Future[DefaultDB]
) extends MongoEntityRepository(database, new UserFormat(sId = sMongoId))
  with UserRepository
