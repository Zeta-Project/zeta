package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue


sealed trait AttributeType {

  val asString: String

}

object AttributeType {

  case object StringType extends AttributeType {

    override val asString = "String"

  }

  case object BoolType extends AttributeType {

    override val asString = "Bool"

  }

  case object IntType extends AttributeType {

    override val asString = "Int"

  }

  case object DoubleType extends AttributeType {

    override val asString = "Double"

  }

  /** The MEnum implementation
   *
   * @param name   the name of the MENum instance
   * @param values the names of the symbols
   */
  case class MEnum(name: String, values: Seq[String]) extends MObject with AttributeType {

    override val asString: String = "MEnum"

    /** The symbols. */
    val symbols: Seq[EnumSymbol] = values.map(value => EnumSymbol(name, value))

    val symbolMap: Map[String, EnumSymbol] = symbols.map(symbol => (symbol.name, symbol)).toMap

  }

  object MEnum {
    implicit val playJsonFormat: Format[MEnum] = Json.format[MEnum]
  }

  implicit val playJsonFormat = new Format[AttributeType] {

    override def writes(typ: AttributeType): JsValue = {
      typ match {
        case StringType => JsString(StringType.asString)
        case BoolType => JsString(BoolType.asString)
        case IntType => JsString(IntType.asString)
        case DoubleType => JsString(DoubleType.asString)
        case enum: MEnum => MEnum.playJsonFormat.writes(enum)
      }
    }

    override def reads(json: JsValue): JsResult[AttributeType] = {
      json match {
        case JsString(StringType.asString) => JsSuccess(StringType)
        case JsString(BoolType.asString) => JsSuccess(BoolType)
        case JsString(IntType.asString) => JsSuccess(IntType)
        case JsString(DoubleType.asString) => JsSuccess(DoubleType)
        case _ => MEnum.playJsonFormat.reads(json)
      }
    }

  }

  def parse(s: String): AttributeType = {
    s match {
      case StringType.asString => StringType
      case BoolType.asString => BoolType
      case IntType.asString => IntType
      case DoubleType.asString => DoubleType
    }
  }

}
