package de.htwg.zeta.persistence.microService

import java.time.Instant
import java.util.UUID

import akka.http.scaladsl.marshalling.Marshal
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.sAbstractness
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.sInputs
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.sOutputs
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.sAttributes
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.sSourceDeletionDeletesTarget
import de.htwg.zeta.persistence.microService.PersistenceJsonProtocol.sTargetDeletionDeletesSource
import models.User
import models.document.PasswordInfoEntity
import models.document.UserEntity
import models.document.Log
import models.document.JobSettings
import models.document.EventDrivenTask
import models.document.BondedTask
import models.document.Generator
import models.document.Filter
import models.document.GeneratorImage
import models.document.FilterImage
import models.document.Settings
import models.document.DockerSettings
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.MObject
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.AttributeType
import models.modelDefinitions.metaModel.elements.ScalarType
import models.modelDefinitions.metaModel.elements.AttributeValue
import models.modelDefinitions.metaModel.elements.ScalarValue
import models.modelDefinitions.metaModel.elements.MBounds
import models.modelDefinitions.metaModel.elements.ClassOrRef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MEnum
import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.model.ModelEntity
import spray.json.DefaultJsonProtocol
import spray.json.JsString
import spray.json.JsValue
import spray.json.RootJsonFormat
import spray.json.deserializationError
import spray.json.JsObject
import spray.json.DeserializationException
import spray.json.JsBoolean
import spray.json.JsNumber
import spray.json.RootJsonFormat
import spray.json.pimpAny
import spray.json.JsonWriter
import spray.json.JsArray
import spray.json.DefaultJsonProtocol.seqFormat
import spray.json.DefaultJsonProtocol.StringJsonFormat

/**
 * Provides implicit conversion for using the Spray-Json library.
 */
object PersistenceJsonProtocol extends DefaultJsonProtocol with App {

  private val sType = "type"
  private val sValue = "value"
  private val sString = "string"
  private val sBool = "bool"
  private val sInt = "int"
  private val sDouble = "double"

  private val sMType = "mType"
  private val sMClass = "mClass"
  private val sMReference = "mReference"
  private val sUpperBound = "upperBound"
  private val sLowerBound = "lowerBound"
  private val sDeleteIfLower = "deleteIfLower"

  private val sName = "name"
  private val sAbstractness = "abstractness"
  private val sSuperTypes = "superTypes"
  private val sInputs = "inputs"
  private val sOutputs = "outputs"
  private val sAttributes = "attributes"

  private val sSourceDeletionDeletesTarget = "sourceDeletionDeletesTarget"
  private val sTargetDeletionDeletesSource = "targetDeletionDeletesSource"
  private val sSource = "source"
  private val sTarget = "target"

  private val sValues = "values"


  /** Spray-Json conversion protocol for [[models.modelDefinitions.metaModel.elements.AttributeType]] */
  private implicit object AttributeTypeFormat extends RootJsonFormat[AttributeType] {

    /** Write a AttributeType.
     *
     * @param attrType AttributeType
     * @return JsValue
     */
    def write(attrType: AttributeType): JsString = {
      attrType match {
        case ScalarType.String => JsString(sString)
        case ScalarType.Bool => JsString(sBool)
        case ScalarType.Int => JsString(sInt)
        case ScalarType.Double => JsString(sDouble)
      }
    }

    /** Read a AttributeType.
     *
     * @param value JsValue
     * @return AttributeType
     */
    def read(value: JsValue): AttributeType = {
      value match {
        case JsString(`sString`) => ScalarType.String
        case JsString(`sBool`) => ScalarType.Bool
        case JsString(`sInt`) => ScalarType.Int
        case JsString(`sDouble`) => ScalarType.Double
      }
    }

  }

  /** Spray-Json conversion protocol for [[models.modelDefinitions.metaModel.elements.AttributeValue]] */
  private implicit object AttributeValueFormat extends RootJsonFormat[AttributeValue] {

    /** Write a AttributeValue.
     *
     * @param attrValue AttributeValue
     * @return JsValue
     */
    def write(attrValue: AttributeValue): JsObject = {
      attrValue match {
        case ScalarValue.MString(v) => JsObject(sType -> JsString(sString), sValue -> JsString(v))
        case ScalarValue.MBool(v) => JsObject(sType -> JsString(sBool), sValue -> JsBoolean(v))
        case ScalarValue.MInt(v) => JsObject(sType -> JsString(sInt), sValue -> JsNumber(v))
        case ScalarValue.MDouble(v) => JsObject(sType -> JsString(sDouble), sValue -> JsNumber(v))
      }
    }

    /** Read a AttributeValue.
     *
     * @param value JsValue
     * @return AttributeValue
     */
    def read(value: JsValue): AttributeValue = {
      value.asJsObject.getFields(sType, sValue) match {
        case Seq(JsString(`sString`), JsString(v)) => ScalarValue.MString(v)
        case Seq(JsString(`sBool`), JsBoolean(v)) => ScalarValue.MBool(v)
        case Seq(JsString(`sInt`), JsNumber(v)) => ScalarValue.MInt(v.toInt)
        case Seq(JsString(`sDouble`), JsNumber(v)) => ScalarValue.MDouble(v.toDouble)
      }
    }

  }

  private implicit val mAttributeFormat: RootJsonFormat[MAttribute] = jsonFormat12(MAttribute.apply)

  /** Spray-Json conversion protocol for [[models.modelDefinitions.metaModel.elements.MLinkDef]] */
  private implicit object MLinkDefFormat extends RootJsonFormat[MLinkDef] {

    /** Write a MLinkDef.
     *
     * @param mLinkDef MLinkDef
     * @return JsObject
     */
    def write(mLinkDef: MLinkDef): JsObject = {

      JsObject(
        sType -> (mLinkDef.mType match {
          case _: MClass => JsString(sMClass)
          case _: MReference => JsString(sMReference)
        }),
        sMType -> (mLinkDef.mType match {
          case mClass: MClass => forwardRefMClassToJson(mClass)
          case mReference: MReference => forwardRefMReferenceToJson(mReference)
        }),
        sUpperBound -> JsNumber(mLinkDef.upperBound),
        sLowerBound -> JsNumber(mLinkDef.lowerBound),
        sDeleteIfLower -> JsBoolean(mLinkDef.deleteIfLower)
      )
    }

    /** Read a MLinkDef.
     *
     * @param value JsValue
     * @return MLinkDef
     */
    def read(value: JsValue): MLinkDef = {
      value.asJsObject.getFields(sType, sMType, sUpperBound, sLowerBound, sDeleteIfLower) match {
        case Seq(JsString(mType), mClassOrRef, JsNumber(upperBound), JsNumber(lowerBound), JsBoolean(deleteIfLower)) =>
          MLinkDef(
            mType match {
              case `sMClass` => forwardRefMClassConvertTo(mClassOrRef)
              case `sMReference` => forwardRefMReferenceConvertTo(mClassOrRef)
            },
            upperBound.toInt,
            lowerBound.toInt,
            deleteIfLower
          )
      }
    }

  }

  /** Spray-Json conversion protocol for [[models.modelDefinitions.metaModel.elements.MClass]] */
  private implicit object MClassFormat extends RootJsonFormat[MClass] {

    /** Write a MClass.
     *
     * @param mClass MClass
     * @return JsObject
     */
    def write(mClass: MClass): JsObject = {
      JsObject(
        sName -> JsString(mClass.name),
        sAbstractness -> JsBoolean(mClass.abstractness),
        sSuperTypes -> mClass.superTypes.toJson,
        sInputs -> mClass.inputs.toJson,
        sOutputs -> mClass.outputs.toJson,
        sAttributes -> mClass.attributes.toJson
      )
    }

    /** Read a MClass.
     *
     * @param value JsValue
     * @return MClass
     */
    def read(value: JsValue): MClass = {
      value.asJsObject.getFields(sName, sAbstractness, sSuperTypes, sInputs, sOutputs, sAttributes) match {
        case Seq(JsString(name), JsBoolean(abstractness), superTypes, inputs, outputs, attributes) =>
          MClass(
            name,
            abstractness,
            superTypes.convertTo[List[MClass]],
            inputs.convertTo[List[MLinkDef]],
            outputs.convertTo[List[MLinkDef]],
            attributes.convertTo[List[MAttribute]]
          )
      }
    }

  }


  /** Spray-Json conversion protocol for [[models.modelDefinitions.metaModel.elements.MReference]] */
  private implicit object MReferenceFormat extends RootJsonFormat[MReference] {

    /** Write a MReference.
     *
     * @param mReference MReference
     * @return JsObject
     */
    def write(mReference: MReference): JsObject = {
      JsObject(
        sName -> JsString(mReference.name),
        sSourceDeletionDeletesTarget -> JsBoolean(mReference.sourceDeletionDeletesTarget),
        sTargetDeletionDeletesSource -> JsBoolean(mReference.targetDeletionDeletesSource),
        sSource -> mReference.source.toJson,
        sTarget -> mReference.target.toJson,
        sAttributes -> mReference.attributes.toJson
      )
    }

    /** Read a MReference.
     *
     * @param value JsValue
     * @return MReference
     */
    def read(value: JsValue): MReference = {
      value.asJsObject.getFields(sName, sSourceDeletionDeletesTarget, sTargetDeletionDeletesSource, sSource, sTarget, sAttributes) match {
        case Seq(JsString(name), JsBoolean(sourceDeletionDeletesTarget), JsBoolean(targetDeletionDeletesSource), source, target, attributes) =>
          MReference(
            name,
            sourceDeletionDeletesTarget,
            targetDeletionDeletesSource,
            source.convertTo[List[MLinkDef]],
            target.convertTo[List[MLinkDef]],
            attributes.convertTo[List[MAttribute]]
          )
      }
    }

  }

  private def forwardRefMClassToJson(mClass: MClass): JsValue = {
    mClass.toJson
  }

  private def forwardRefMReferenceToJson(mReference: MReference): JsValue = {
    mReference.toJson
  }

  private def forwardRefMClassConvertTo(mClass: JsValue): MClass = {
    mClass.convertTo[MClass]
  }

  private def forwardRefMReferenceConvertTo(mReference: JsValue): MClass = {
    mReference.convertTo[MClass]
  }


  /** Spray-Json conversion protocol for [[models.modelDefinitions.metaModel.elements.MReference]] */
  private implicit object MEnumFormat extends RootJsonFormat[MEnum] {

    /** Write a MEnum.
     *
     * @param mEnum MEnum
     * @return JsObject
     */
    def write(mEnum: MEnum): JsObject = {
      JsObject(
        sName -> JsString(mEnum.name),
        sValues -> JsArray(mEnum.values.map(value => JsString(value.name)).toVector)
      )
    }

    /** Read a MEnum.
     *
     * @param value JsValue
     * @return MEnum
     */
    def read(value: JsValue): MEnum = {
      value.asJsObject.getFields(sName, sValues) match {
        case Seq(JsString(name), JsArray(values)) =>
          val enum = MEnum(name, null) // scalastyle:ignore
          MEnum(
            name,
            values.map {
              case JsString(v) => EnumSymbol(v, enum)
            }
          )
      }
    }

  }


  /** Spray-Json conversion protocol for [[java.time.Instant]] */
  private implicit object InstantFormat extends RootJsonFormat[Instant] {

    /** Write a Instant.
     *
     * @param instant Instant
     * @return JsObject
     */
    def write(instant: Instant): JsString = {
      JsString(instant.toString)
    }

    /** Read a Instant.
     *
     * @param value JsValue
     * @return Instant
     */
    def read(value: JsValue): Instant = {
      Instant.parse(value.toString)
    }

  }

  // private implicit val metaModelFormat: RootJsonFormat[MetaModel] = jsonFormat3(MetaModel.apply)


  /** Spray-Json conversion protocol for [[models.document.EventDrivenTask]] */
  implicit val eventDrivenTaskFormat: RootJsonFormat[EventDrivenTask] = jsonFormat6(EventDrivenTask.apply)

  /** Spray-Json conversion protocol for [[models.document.BondedTask]] */
  implicit val bondedTaskFormat: RootJsonFormat[BondedTask] = jsonFormat7(BondedTask.apply)

  /** Spray-Json conversion protocol for [[models.document.Generator]] */
  implicit val generatorFormat: RootJsonFormat[Generator] = jsonFormat4(Generator.apply)

  /** Spray-Json conversion protocol for [[models.document.Filter]] */
  implicit val filterFormat: RootJsonFormat[Filter] = jsonFormat5(Filter.apply)

  /** Spray-Json conversion protocol for [[models.document.GeneratorImage]] */
  implicit val generatorImageFormat: RootJsonFormat[GeneratorImage] = jsonFormat4(GeneratorImage.apply)

  /** Spray-Json conversion protocol for [[models.document.FilterImage]] */
  implicit val filterImageFormat: RootJsonFormat[FilterImage] = jsonFormat4(FilterImage.apply)

  /** Spray-Json conversion protocol for [[models.document.Settings]] */
  implicit val settingsFormat: RootJsonFormat[Settings] = {
    implicit val dockerSettingsFormat: RootJsonFormat[DockerSettings] = jsonFormat2(DockerSettings.apply)
    implicit val jobSettingsInfoFormat: RootJsonFormat[JobSettings] = jsonFormat3(JobSettings.apply)
    jsonFormat4(Settings.apply)
  }

  /** Spray-Json conversion protocol for [[models.document.MetaModelEntity]] */
  // TODO: implicit val metaModelEntityFormat: RootJsonFormat[MetaModelEntity] = jsonFormat6(MetaModelEntity.apply)

  /** Spray-Json conversion protocol for [[models.document.MetaModelRelease]] */
  // TODO: implicit val metaModelReleaseFormat: RootJsonFormat[MetaModelRelease] = jsonFormat6(MetaModelRelease.apply)

  /** Spray-Json conversion protocol for [[models.document.ModelEntity]] */
  //implicit val modelEntityFormat: RootJsonFormat[ModelEntity] = jsonFormat7(ModelEntity.apply)

  /** Spray-Json conversion protocol for [[models.document.Log]] */
  implicit val logFormat: RootJsonFormat[Log] = jsonFormat5(Log.apply)

  /** Spray-Json conversion protocol for [[models.document.UserEntity]] */
  implicit val passwordInfoEntityFormat: RootJsonFormat[PasswordInfoEntity] = {
    implicit val passwordInfo: RootJsonFormat[PasswordInfo] = jsonFormat3(PasswordInfo)
    jsonFormat3(PasswordInfoEntity.apply)
  }

  /** Spray-Json conversion protocol for [[models.document.UserEntity]] */
  implicit val userEntityFormat: RootJsonFormat[UserEntity] = {

    implicit object UuidJsonFormat extends RootJsonFormat[UUID] {

      /** Write a UUID.
       *
       * @param x UUID
       * @return JsValue
       */
      def write(x: UUID): JsValue = {
        JsString(x.toString)
      }

      /** Read a UUID.
       *
       * @param value JsValue
       * @return UUID
       */
      def read(value: JsValue): UUID = {
        value match { // TODO pattern match not needed for single value
          case JsString(x) => UUID.fromString(x)
        }
      }

    }

    implicit val loginInfoFormat: RootJsonFormat[LoginInfo] = jsonFormat2(LoginInfo)
    implicit val userFormat: RootJsonFormat[User] = jsonFormat8(User.apply)
    jsonFormat3(UserEntity.apply)


  }


}

