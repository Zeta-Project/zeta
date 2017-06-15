package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue


sealed trait AttributeType

object AttributeType {

  case object StringType extends AttributeType

  case object BoolType extends AttributeType

  case object IntType extends AttributeType

  case object DoubleType extends AttributeType

  /** The MEnum implementation
   *
   * @param name   the name of the MENum instance
   * @param values the names of the symbols
   */
  case class MEnum(name: String, values: Set[String]) extends MObject with AttributeType {

    /** The symbols. */
    val symbols: Set[EnumSymbol] = values.map(value => EnumSymbol(name, value))

  }

  object MEnum {
    implicit val playJsonFormat: Format[MEnum] = Json.format[MEnum]
  }

  implicit val playJsonFormat = new Format[AttributeType] {

    private val sString = "string"
    private val sBool = "bool"
    private val sInt = "int"
    private val sDouble = "double"

    override def writes(typ: AttributeType): JsValue = {
      typ match {
        case StringType => JsString(sString)
        case BoolType => JsString(sBool)
        case IntType => JsString(sInt)
        case DoubleType => JsString(sDouble)
        case enum: MEnum => MEnum.playJsonFormat.writes(enum)
      }
    }

    override def reads(json: JsValue): JsResult[AttributeType] = {
      json match {
        case JsString(`sString`) => JsSuccess(StringType)
        case JsString(`sBool`) => JsSuccess(BoolType)
        case JsString(`sInt`) => JsSuccess(IntType)
        case JsString(`sDouble`) => JsSuccess(DoubleType)
        case _ => MEnum.playJsonFormat.reads(json)
      }
    }

  }

}
