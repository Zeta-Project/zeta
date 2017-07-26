package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes


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

  case object UnitType extends AttributeType {

    override val asString = "Unit"

  }

  /** The MEnum implementation
   *
   * @param name   the name of the MENum instance
   * @param values the names of the symbols
   */
  case class MEnum(name: String, values: Seq[String]) extends MObject with AttributeType {

    override val asString: String = name

    /** The symbols. */
    val symbols: Seq[EnumSymbol] = values.map(value => EnumSymbol(name, value))

    val symbolMap: Map[String, EnumSymbol] = symbols.map(symbol => (symbol.name, symbol)).toMap

  }

  object MEnum {
    implicit val playJsonFormat: Format[MEnum] = Json.format[MEnum]
  }

  def playJsonReads(enums: Seq[MEnum]): Reads[AttributeType] = {
    new Reads[AttributeType] {
      override def reads(json: JsValue): JsResult[AttributeType] = {
        json.validate[String].map {
          case StringType.asString => StringType
          case BoolType.asString => BoolType
          case IntType.asString => IntType
          case DoubleType.asString => DoubleType
          case UnitType.asString => UnitType
          case enumName: String => enums.find(_.name == enumName).get
        }
      }
    }
  }

  implicit val playJsonWrites = new Writes[AttributeType] {
    override def writes(typ: AttributeType): JsValue = {
      typ match {
        case StringType => JsString(StringType.asString)
        case BoolType => JsString(BoolType.asString)
        case IntType => JsString(IntType.asString)
        case DoubleType => JsString(DoubleType.asString)
        case UnitType => JsString(UnitType.asString)
        case enum: MEnum => MEnum.playJsonFormat.writes(enum)
      }
    }
  }

  def parse(s: String): AttributeType = {
    s match {
      case StringType.asString => StringType
      case BoolType.asString => BoolType
      case IntType.asString => IntType
      case DoubleType.asString => DoubleType
      case UnitType.asString => UnitType
    }
  }

}
