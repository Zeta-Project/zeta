package de.htwg.zeta.persistence.mongo

import java.util.UUID

import scala.collection.immutable.Seq
import scala.collection.immutable.SortedMap

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import de.htwg.zeta.common.models.document.DockerSettings
import de.htwg.zeta.common.models.document.JobSettings
import de.htwg.zeta.common.models.entity.AccessAuthorisation
import de.htwg.zeta.common.models.entity.BondedTask
import de.htwg.zeta.common.models.entity.EventDrivenTask
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.FilterImage
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.GeneratorImageOptions
import de.htwg.zeta.common.models.entity.GeneratorMetaModelReleaseProperty
import de.htwg.zeta.common.models.entity.GeneratorNameProperty
import de.htwg.zeta.common.models.entity.GeneratorOptionProperties
import de.htwg.zeta.common.models.entity.Log
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.entity.Settings
import de.htwg.zeta.common.models.entity.TimedTask
import de.htwg.zeta.common.models.entity.User
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Diagram
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Dsl
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Shape
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Style
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.UnitType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.EdgeLink
import de.htwg.zeta.common.models.modelDefinitions.model.elements.NodeLink
import reactivemongo.bson.BSONArray
import reactivemongo.bson.BSONBoolean
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentHandler
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.BSONDouble
import reactivemongo.bson.BSONInteger
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

  case class UserIdOnlyEntity(userId: UUID)

  implicit val userIdOnlyEntityHandler: BSONDocumentHandler[UserIdOnlyEntity] = Macros.handler[UserIdOnlyEntity]

  case class LoginInfoWrapper(loginInfo: LoginInfo)

  implicit val loginInfoWrapperHandler: BSONDocumentHandler[LoginInfoWrapper] = Macros.handler[LoginInfoWrapper]

  case class PasswordInfoWrapper(authInfo: PasswordInfo)

  implicit val passwordInfoWrapperHandler: BSONDocumentHandler[PasswordInfoWrapper] = Macros.handler[PasswordInfoWrapper]

  private val uuidSetHandler = new {

    def read(doc: BSONArray): Set[UUID] = {
      doc.values.map { case s: BSONString => IdHandler.read(s) }.toSet
    }

    def write(set: Set[UUID]): BSONArray = {
      BSONArray(set.map(IdHandler.write).toList)
    }

  }

  private implicit object MapStringSetIdHandler extends BSONDocumentReader[Map[String, Set[UUID]]] with BSONDocumentWriter[Map[String, Set[UUID]]] {

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

  private implicit val generatorMMRPropertyHandler: BSONDocumentHandler[GeneratorMetaModelReleaseProperty] = Macros.handler[GeneratorMetaModelReleaseProperty]
  private implicit val generatorNamePropertyHandler: BSONDocumentHandler[GeneratorNameProperty] = Macros.handler[GeneratorNameProperty]
  private implicit val generatorOptionPropertiesHandler: BSONDocumentHandler[GeneratorOptionProperties] = Macros.handler[GeneratorOptionProperties]
  private implicit val generatorImageOptionsHandler: BSONDocumentHandler[GeneratorImageOptions] = Macros.handler[GeneratorImageOptions]
  implicit val generatorImageHandler: BSONDocumentHandler[GeneratorImage] = Macros.handler[GeneratorImage]

  implicit val filterImageHandler: BSONDocumentHandler[FilterImage] = Macros.handler[FilterImage]

  private implicit val jobSettingsHandler: BSONDocumentHandler[JobSettings] = Macros.handler[JobSettings]

  private implicit val dockerSettingsHandler: BSONDocumentHandler[DockerSettings] = Macros.handler[DockerSettings]

  implicit val settingsHandler: BSONDocumentHandler[Settings] = Macros.handler[Settings]

  private val sType = "type"
  private val sValue = "value"
  private val sValues = "values"
  private val sString = "string"
  private val sBool = "bool"
  private val sInt = "int"
  private val sDouble = "double"
  private val sUnit = "unit"
  private val sEnum = "enum"
  private val sName = "name"
  private val sEnumName = "enumName"

  private implicit object AttributeTypeHandler extends BSONDocumentWriter[AttributeType] with BSONDocumentReader[AttributeType] {

    override def write(typ: AttributeType): BSONDocument = {
      typ match {
        case StringType => BSONDocument(sType -> sString)
        case BoolType => BSONDocument(sType -> sBool)
        case IntType => BSONDocument(sType -> sInt)
        case DoubleType => BSONDocument(sType -> sDouble)
        case UnitType => BSONDocument(sType -> sUnit)
        case MEnum(name, values) => BSONDocument(sType -> sEnum, sName -> name, sValues -> values)
      }
    }

    override def read(doc: BSONDocument): AttributeType = {
      doc.getAs[String](sType).get match {
        case `sString` => StringType
        case `sBool` => BoolType
        case `sInt` => IntType
        case `sDouble` => DoubleType
        case `sUnit` => UnitType
        case `sEnum` => MEnum(doc.getAs[String](sName).get, doc.getAs[Seq[String]](sValues).get)
      }
    }

  }

  private implicit object AttributeValueHandler extends BSONDocumentWriter[AttributeValue] with BSONDocumentReader[AttributeValue] {

    override def write(value: AttributeValue): BSONDocument = {
      value match {
        case StringValue(v) => BSONDocument(sType -> sString, sValue -> BSONString(v))
        case BoolValue(v) => BSONDocument(sType -> sBool, sValue -> BSONBoolean(v))
        case IntValue(v) => BSONDocument(sType -> sInt, sValue -> BSONInteger(v))
        case DoubleValue(v) => BSONDocument(sType -> sDouble, sValue -> BSONDouble(v))
        case EnumValue(name, enumName) => BSONDocument(sType -> sEnum, sName -> name, sEnumName -> enumName)
      }
    }

    override def read(doc: BSONDocument): AttributeValue = {
      doc.getAs[String](sType).get match {
        case `sString` => StringValue(doc.getAs[String](sValue).get)
        case `sBool` => BoolValue(doc.getAs[Boolean](sValue).get)
        case `sInt` => IntValue(doc.getAs[Int](sValue).get)
        case `sDouble` => DoubleValue(doc.getAs[Double](sValue).get)
        case `sEnum` => EnumValue(doc.getAs[String](sName).get, doc.getAs[String](sEnumName).get)
      }
    }

  }

  private implicit val mAttributeHandler: BSONDocumentHandler[MAttribute] = Macros.handler[MAttribute]

  private implicit object ParametersHandler extends BSONDocumentWriter[SortedMap[String, AttributeType]]
    with BSONDocumentReader[SortedMap[String, AttributeType]] {

    override def read(doc: BSONDocument): SortedMap[String, AttributeType] = {
      SortedMap(doc.elements.map { tuple =>
        tuple.name -> AttributeTypeHandler.read(tuple.value.seeAsTry[BSONDocument].get)
      }: _*)
    }

    override def write(parameters: SortedMap[String, AttributeType]): BSONDocument = {
      BSONDocument(parameters.map { tuple =>
        tuple._1 -> AttributeTypeHandler.write(tuple._2)
      })
    }

  }

  private implicit val methodHandler: BSONDocumentHandler[Method] = Macros.handler[Method]

  private implicit val mClassHandler: BSONDocumentHandler[MClass] = Macros.handler[MClass]

  private implicit val mReferenceHandler: BSONDocumentHandler[MReference] = Macros.handler[MReference]

  private implicit val mReferenceLinkDefHandler: BSONDocumentHandler[MReferenceLinkDef] = Macros.handler[MReferenceLinkDef]

  private implicit val mClassLinkDefHandler: BSONDocumentHandler[MClassLinkDef] = Macros.handler[MClassLinkDef]

  private implicit val metaModelHandler: BSONDocumentHandler[MetaModel] = Macros.handler[MetaModel]

  private implicit val mEnumHandler: BSONDocumentHandler[MEnum] = Macros.handler[MEnum]

  private implicit val dslHandler: BSONDocumentHandler[Dsl] = Macros.handler[Dsl]

  private implicit val diagramHandler: BSONDocumentHandler[Diagram] = Macros.handler[Diagram]

  private implicit val shapeHandler: BSONDocumentHandler[Shape] = Macros.handler[Shape]

  private implicit val styleHandler: BSONDocumentHandler[Style] = Macros.handler[Style]

  implicit val metaModelEntityHandler: BSONDocumentHandler[MetaModelEntity] = Macros.handler[MetaModelEntity]

  implicit val metaModelReleaseHandler: BSONDocumentHandler[MetaModelRelease] = Macros.handler[MetaModelRelease]

  implicit val modelEntityHandler: BSONDocumentHandler[ModelEntity] = Macros.handler[ModelEntity]

  implicit val nodeEntityHandler: BSONDocumentHandler[Node] = Macros.handler[Node]

  implicit val edgeEntityHandler: BSONDocumentHandler[Edge] = Macros.handler[Edge]

  implicit val toNodesEntityHandler: BSONDocumentHandler[NodeLink] = Macros.handler[NodeLink]

  implicit val toEdgesEntityHandler: BSONDocumentHandler[EdgeLink] = Macros.handler[EdgeLink]

  implicit val modelHandler: BSONDocumentHandler[Model] = Macros.handler[Model]

  implicit val logHandler: BSONDocumentHandler[Log] = Macros.handler[Log]

  implicit val userHandler: BSONDocumentHandler[User] = Macros.handler[User]

  implicit val fileHandler: BSONDocumentHandler[File] = Macros.handler[File]

  implicit val loginInfoHandler: BSONDocumentHandler[LoginInfo] = Macros.handler[LoginInfo]

  implicit val passwordInfoHandler: BSONDocumentHandler[PasswordInfo] = Macros.handler[PasswordInfo]

}
