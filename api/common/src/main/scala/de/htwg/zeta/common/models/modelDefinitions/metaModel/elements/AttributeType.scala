package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumValue
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

    override val asString = "Boolean"

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
   * @param name       the name of the MENum instance
   * @param valueNames the names of the values
   */
  case class MEnum(name: String, valueNames: Seq[String]) extends MObject with AttributeType {

    override val asString: String = name

    /** The symbols. */
    val values: Seq[EnumValue] = valueNames.map(value => EnumValue(name, value))

    val valueMap: Map[String, EnumValue] = values.map(symbol => (symbol.name, symbol)).toMap

  }

  object MEnum {

    trait EnumMap {

      val enums: Seq[MEnum]

      /** Enums mapped to their own names. */
      final val enumMap: Map[String, MEnum] = Option(enums).fold(
        Map.empty[String, MEnum]
      ) { enums =>
        enums.filter(Option(_).isDefined).map(enum => (enum.name, enum)).toMap
      }
    }

    implicit val playJsonFormat: Format[MEnum] = Json.format[MEnum]

  }

  def playJsonReads(enums: Seq[MEnum]): Reads[AttributeType] = new Reads[AttributeType] {
    override def reads(json: JsValue): JsResult[AttributeType] = {
      json.validate[String].map {
        case StringType.asString => StringType
        case BoolType.asString => BoolType
        case "Bool" => BoolType // TODO frontend should always send "Boolean" instead of "Bool"
        case IntType.asString => IntType
        case DoubleType.asString => DoubleType
        case UnitType.asString => UnitType
        case enumName: String => enums.find(_.name == enumName).get
      }
    }
  }

  implicit val playJsonWrites: Writes[AttributeType] = new Writes[AttributeType] {
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
