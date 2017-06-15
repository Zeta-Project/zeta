package de.htwg.zeta.persistence.mongo

import java.util.UUID

import scala.collection.immutable.SortedMap

import de.htwg.zeta.common.models.document.DockerSettings
import de.htwg.zeta.common.models.document.JobSettings
import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.Entity
import de.htwg.zeta.common.models.entity.EventDrivenTask
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.FilterImage
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.Log
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.entity.Settings
import de.htwg.zeta.common.models.entity.TimedTask
import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.common.models.modelDefinitions.helper.HLink
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Diagram
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Dsl
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Shape
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Style
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToEdges
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes
import reactivemongo.bson.BSONArray
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentHandler
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONReader
import reactivemongo.bson.BSONString
import reactivemongo.bson.BSONWriter
import reactivemongo.bson.Macros


object MongoHandler {

  implicit object IdHandler extends BSONReader[BSONString, UUID] with BSONWriter[UUID, BSONString] {

    def read(doc: BSONString): UUID = {
      UUID.fromString(doc.value)
    }

    def write(id: UUID): BSONString = {
      BSONString(id.toString)
    }

  }

  case class IdOnlyEntity(id: UUID)

  implicit val idOnlyEntityHandler: BSONDocumentHandler[IdOnlyEntity] = Macros.handler[IdOnlyEntity]

  case class FileKey(id: UUID, name: String)

  implicit val fileKeyHandler: BSONDocumentHandler[FileKey] = Macros.handler[FileKey]

  private val uuidSetHandler = new {

    def read(doc: BSONArray): Set[UUID] = {
      doc.values.map { case s: BSONString => IdHandler.read(s) }.toSet
    }

    def write(set: Set[UUID]): BSONArray = {
      BSONArray(set.map(IdHandler.write).toList)
    }

  }

  private implicit val mapStringSetIdHandler = new BSONDocumentReader[Map[String, Set[UUID]]] with BSONDocumentWriter[Map[String, Set[UUID]]] {

    override def read(doc: BSONDocument): Map[String, Set[UUID]] = {
      doc.elements.map { tuple =>
        tuple.name -> uuidSetHandler.read(tuple.value.seeAsTry[BSONArray].get)
      }.toMap
    }

    override def write(map: Map[String, Set[UUID]]): BSONDocument = {
      BSONDocument(map.map { tuple =>
        tuple._1 -> uuidSetHandler.write(tuple._2)
      })
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


  private implicit val jobSettingsHandler: BSONDocumentHandler[JobSettings] = Macros.handler[JobSettings]

  private implicit val dockerSettingsHandler: BSONDocumentHandler[DockerSettings] = Macros.handler[DockerSettings]

  implicit val settingsHandler: BSONDocumentHandler[Settings] = Macros.handler[Settings]


  private implicit val attributeTypeHandler: BSONDocumentHandler[AttributeType] = Macros.handler[AttributeType]

  private implicit val attributeValueHandler: BSONDocumentHandler[AttributeValue] = Macros.handler[AttributeValue]

  private implicit val mAttributeHandler: BSONDocumentHandler[MAttribute] = Macros.handler[MAttribute]

  private implicit val mClassHandler: BSONDocumentHandler[MClass] = Macros.handler[MClass]

  private implicit val mReferenceHandler: BSONDocumentHandler[MReference] = Macros.handler[MReference]

  private implicit val mReferenceLinkDefHandler: BSONDocumentHandler[MReferenceLinkDef] = Macros.handler[MReferenceLinkDef]

  private implicit val mClassLinkDefHandler: BSONDocumentHandler[MClassLinkDef] = Macros.handler[MClassLinkDef]

  private implicit val metaModelHandler: BSONDocumentHandler[MetaModel] = Macros.handler[MetaModel]

  private implicit val mapMClassHandler = new BSONDocumentReader[Map[String, MClass]] with BSONDocumentWriter[Map[String, MClass]] {

    override def read(doc: BSONDocument): Map[String, MClass] = {
      doc.values.map { value =>
        mClassHandler.read(value.seeAsTry[BSONDocument].get)
      }.map(c => (c.name, c)).toMap
    }

    override def write(map: Map[String, MClass]): BSONDocument = {
      BSONDocument(map.values.map(mClassHandler.write))
    }

  }


  private implicit val mapMReferenceHandler = new BSONDocumentReader[Map[String, MReference]] with BSONDocumentWriter[Map[String, MReference]] {

    override def read(doc: BSONDocument): Map[String, MReference] = {
      doc.values.map { value =>
        mReferenceHandler.read(value.seeAsTry[BSONDocument].get)
      }.map(c => (c.name, c)).toMap
    }

    override def write(map: Map[String, MReference]): BSONDocument = {
      BSONDocument(map.values.map(mReferenceHandler.write))
    }

  }

  private implicit val mEnumHandler: BSONDocumentHandler[MEnum] = Macros.handler[MEnum]

  private implicit val mapMEumHandler = new BSONDocumentReader[Map[String, MEnum]] with BSONDocumentWriter[Map[String, MEnum]] {

    override def read(doc: BSONDocument): Map[String, MEnum] = {
      doc.values.map { value =>
        mEnumHandler.read(value.seeAsTry[BSONDocument].get)
      }.map(c => (c.name, c)).toMap
    }

    override def write(map: Map[String, MEnum]): BSONDocument = {
      BSONDocument(map.values.map(mEnumHandler.write))
    }

  }

  private implicit val dslHandler: BSONDocumentHandler[Dsl] = Macros.handler[Dsl]

  private implicit val diagramHandler: BSONDocumentHandler[Diagram] = Macros.handler[Diagram]

  private implicit val hLinkHandler: BSONDocumentHandler[HLink] = Macros.handler[HLink]

  private implicit val shapeHandler: BSONDocumentHandler[Shape] = Macros.handler[Shape]

  private implicit val styleHandler: BSONDocumentHandler[Style] = Macros.handler[Style]

  implicit val metaModelEntityHandler: BSONDocumentHandler[MetaModelEntity] = Macros.handler[MetaModelEntity]


  private implicit val sortedMapStringUUIDHandler = new BSONDocumentReader[SortedMap[String, UUID]] with BSONDocumentWriter[SortedMap[String, UUID]] {

    override def read(doc: BSONDocument): SortedMap[String, UUID] = {
      val m = doc.elements.map { tuple =>
        tuple.name -> IdHandler.read(tuple.value.seeAsTry[BSONString].get)
      }.toMap
      scala.collection.immutable.TreeMap(m.toArray: _*)
    }

    override def write(map: SortedMap[String, UUID]): BSONDocument = {
      BSONDocument(map.map { tuple =>
        tuple._1 -> IdHandler.write(tuple._2)
      })
    }

  }


  private implicit val entityHandler = new BSONDocumentReader[Entity] with BSONDocumentWriter[Entity] {

    override def read(doc: BSONDocument): Entity = {
      null // TODO
    }

    override def write(entity: Entity): BSONDocument = {
      null // TODO
    }

  }

  implicit val metaModelReleaseHandler: BSONDocumentHandler[MetaModelRelease] = Macros.handler[MetaModelRelease]

  implicit val modelEntityHandler: BSONDocumentHandler[ModelEntity] = Macros.handler[ModelEntity]

  private implicit val mapAttributeValueHandler =
    new BSONDocumentReader[Map[String, scala.collection.immutable.Seq[AttributeValue]]]
      with BSONDocumentWriter[Map[String, scala.collection.immutable.Seq[AttributeValue]]] {

      override def read(doc: BSONDocument): Map[String, scala.collection.immutable.Seq[AttributeValue]] = {
        null // TODO
      }

      override def write(map: Map[String, scala.collection.immutable.Seq[AttributeValue]]): BSONDocument = {
        null // TODO
      }

    }


  private implicit val mapNodeHandler = new BSONDocumentReader[Map[String, Node]] with BSONDocumentWriter[Map[String, Node]] {

    override def read(doc: BSONDocument): Map[String, Node] = {
      null // TODO
    }

    override def write(map: Map[String, Node]): BSONDocument = {
      null // TODO
    }

  }

  private implicit val mapEdgeHandler = new BSONDocumentReader[Map[String, Edge]] with BSONDocumentWriter[Map[String, Edge]] {

    override def read(doc: BSONDocument): Map[String, Edge] = {
      null // TODO
    }

    override def write(map: Map[String, Edge]): BSONDocument = {
      null // TODO
    }

  }


  implicit val nodeEntityHandler: BSONDocumentHandler[Node] = Macros.handler[Node]

  implicit val edgeEntityHandler: BSONDocumentHandler[Edge] = Macros.handler[Edge]

  implicit val toNodesEntityHandler: BSONDocumentHandler[ToNodes] = Macros.handler[ToNodes]

  implicit val toEdgesEntityHandler: BSONDocumentHandler[ToEdges] = Macros.handler[ToEdges]

  implicit val modelHandler: BSONDocumentHandler[Model] = Macros.handler[Model]


  implicit val logHandler: BSONDocumentHandler[Log] = Macros.handler[Log]

  implicit val userHandler: BSONDocumentHandler[User] = Macros.handler[User]


  implicit val fileHandler: BSONDocumentHandler[File] = Macros.handler[File]

}
