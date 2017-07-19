package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import play.api.libs.json.Format
import play.api.libs.json.JsArray
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

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

    private val sString = "string"
    private val sBool = "bool"
    private val sInt = "int"
    private val sDouble = "double"

    override def writes(value: AttributeValue): JsValue = {
      value match {
        case MString(v) => JsObject(Map(sString -> JsString(v)))
        case MBool(v) => JsObject(Map(sBool -> JsBoolean(v)))
        case MInt(v) => JsObject(Map(sInt -> JsNumber(v)))
        case MDouble(v) => JsObject(Map(sDouble -> JsNumber(v)))
        case EnumSymbol(name, enumName) => JsArray(Seq(JsString(name), JsString(enumName)))
      }
    }

    override def reads(json: JsValue): JsResult[AttributeValue] = {
      json match {
        case JsObject(Seq(JsString(`sString`), JsString(v))) => JsSuccess(MString(v))
        case JsObject(Seq(JsString(`sBool`), JsBoolean(v))) => JsSuccess(MBool(v))
        case JsObject(Seq(JsString(`sInt`), JsNumber(v))) => JsSuccess(MInt(v.toInt))
        case JsObject(Seq(JsString(`sDouble`), JsNumber(v))) => JsSuccess(MDouble(v.toDouble))
        case JsArray(Seq(JsString(name), JsString(enumName))) => JsSuccess(EnumSymbol(name, enumName))
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
