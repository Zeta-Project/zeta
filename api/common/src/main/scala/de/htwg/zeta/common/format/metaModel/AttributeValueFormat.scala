package de.htwg.zeta.common.format.metaModel

import scala.collection.immutable.Seq
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import play.api.libs.json.Format
import play.api.libs.json.JsArray
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsError
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat
import play.api.libs.json.Reads

object AttributeValueFormat {

   val playJsonFormat: Format[AttributeValue] = new Format[AttributeValue] {

    override def writes(value: AttributeValue): JsValue = {
      value match {
        case StringValue(v) => JsObject(Map(StringType.asString -> JsString(v)))
        case BoolValue(v) => JsObject(Map(BoolType.asString -> JsBoolean(v)))
        case IntValue(v) => JsObject(Map(IntType.asString -> JsNumber(v)))
        case DoubleValue(v) => JsObject(Map(DoubleType.asString -> JsNumber(v)))
        case EnumValue(name, enumName) => JsArray(Seq(JsString(name), JsString(enumName)))
      }
    }

    override def reads(json: JsValue): JsResult[AttributeValue] = {
      json match {
        case JsObject(Seq(JsString(StringType.asString), JsString(v))) => JsSuccess(StringValue(v))
        case JsObject(Seq(JsString(BoolType.asString), JsBoolean(v))) => JsSuccess(BoolValue(v))
        case JsObject(Seq(JsString(IntType.asString), JsNumber(v))) => JsSuccess(IntValue(v.toInt))
        case JsObject(Seq(JsString(DoubleType.asString), JsNumber(v))) => JsSuccess(DoubleValue(v.toDouble))
        case JsArray(Seq(JsString(name), JsString(enumName))) => JsSuccess(EnumValue(name, enumName))
      }
    }

  }

  def playJsonReads(metaModel: MetaModel, metaAttributes: Seq[MAttribute]): Reads[Map[String, Seq[AttributeValue]]] = Reads { json =>
    Try {
      metaAttributes.map { metaAttribute =>
        val rawAttribute = (json \ metaAttribute.name).validate[List[String]].getOrElse(List.empty)
        val attribute = if (rawAttribute.nonEmpty) {
          metaAttribute.typ match {
            case StringType => rawAttribute.map(StringValue)
            case BoolType => rawAttribute.map(v => BoolValue(v.toBoolean))
            case IntType => rawAttribute.map(v => IntValue(v.toInt))
            case DoubleType => rawAttribute.map(v => DoubleValue(v.toDouble))
            case enum: MEnum => rawAttribute.map(enum.valueMap)
          }
        } else {
          List(metaAttribute.default)
        }
        (metaAttribute.name, attribute)
      }.toMap
    } match {
      case Success(s) => JsSuccess(s)
      case Failure(e) => JsError(e.getMessage)
    }
  }

  /** playJsonReads which overwrites attributes with the same name in the MetaModel from the Model. */
  def playJsonReads(metaModel: MetaModel, metaAttributes: Seq[MAttribute], attributes: Seq[MAttribute]): Reads[Map[String, Seq[AttributeValue]]] = {
    playJsonReads(metaModel, metaAttributes.filter(a => !attributes.map(_.name).contains(a.name)) ++ attributes)
  }

  val enumValueFormat: OFormat[EnumValue] = Json.format[EnumValue]

}
