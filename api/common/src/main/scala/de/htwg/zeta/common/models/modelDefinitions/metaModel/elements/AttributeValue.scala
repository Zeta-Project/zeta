package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

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
import play.api.libs.json.Format
import play.api.libs.json.JsArray
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsError
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads

sealed trait AttributeValue

object AttributeValue {

  case class MString(value: String) extends AttributeValue {
    val attributeType = StringType
  }

  case class MBool(value: Boolean) extends AttributeValue {
    val attributeType = BoolType
  }

  case class MInt(value: Int) extends AttributeValue {
    val attributeType = IntType
  }

  case class MDouble(value: Double) extends AttributeValue {
    val attributeType = DoubleType
  }

  implicit val playJsonFormat = new Format[AttributeValue] {

    override def writes(value: AttributeValue): JsValue = {
      value match {
        case MString(v) => JsObject(Map(StringType.asString -> JsString(v)))
        case MBool(v) => JsObject(Map(BoolType.asString -> JsBoolean(v)))
        case MInt(v) => JsObject(Map(IntType.asString -> JsNumber(v)))
        case MDouble(v) => JsObject(Map(DoubleType.asString -> JsNumber(v)))
        case EnumSymbol(name, enumName) => JsArray(Seq(JsString(name), JsString(enumName)))
      }
    }

    override def reads(json: JsValue): JsResult[AttributeValue] = {
      json match {
        case JsObject(Seq(JsString(StringType.asString), JsString(v))) => JsSuccess(MString(v))
        case JsObject(Seq(JsString(BoolType.asString), JsBoolean(v))) => JsSuccess(MBool(v))
        case JsObject(Seq(JsString(IntType.asString), JsNumber(v))) => JsSuccess(MInt(v.toInt))
        case JsObject(Seq(JsString(DoubleType.asString), JsNumber(v))) => JsSuccess(MDouble(v.toDouble))
        case JsArray(Seq(JsString(name), JsString(enumName))) => JsSuccess(EnumSymbol(name, enumName))
      }
    }

  }

  def playJsonReads(metaModel: MetaModel, metaAttributes: Seq[MAttribute]): Reads[Map[String, Seq[AttributeValue]]] = {
    new Reads[Map[String, Seq[AttributeValue]]] {
      override def reads(json: JsValue): JsResult[Map[String, Seq[AttributeValue]]] = {
        Try {
          metaAttributes.map { metaAttribute =>
            (metaAttribute.name, metaAttribute.typ match {
              case StringType => (json \ metaAttribute.name).validate[List[String]].map(_.map(MString)).get
              case BoolType => (json \ metaAttribute.name).validate[List[Boolean]].map(_.map(MBool)).get
              case IntType => (json \ metaAttribute.name).validate[List[Int]].map(_.map(MInt)).get
              case DoubleType => (json \ metaAttribute.name).validate[List[Double]].map(_.map(MDouble)).get
              case enum: MEnum => (json \ metaAttribute.name).validate[List[String]].map(_.map(enum.symbolMap)).get
            })
          }.toMap
        } match {
          case Success(s) => JsSuccess(s)
          case Failure(e) => JsError(e.getMessage)
        }
      }
    }
  }

  /** An Enum Symbol
   *
   * @param name     name of the symbol
   * @param enumName name of the the belonging MEnum
   */
  case class EnumSymbol(enumName: String, name: String) extends AttributeValue

  object EnumSymbol {

    implicit val playJsonFormat: OFormat[EnumSymbol] = Json.format[EnumSymbol]

  }

}
