package de.htwg.zeta.common.models.project.concept.elements

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.EnumValue
import play.api.libs.json.JsObject
import play.api.libs.json.Json


sealed trait AttributeType {

  val asString: String

  def asJson: JsObject = Json.obj("asString" -> asString)
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

  case class EnumType(name: String) extends AttributeType {

    override val asString: String = name

  }

  /** The MEnum implementation
   *
   * @param name       the name of the MENum instance
   * @param valueNames the names of the values
   */
  case class MEnum(name: String, valueNames: Seq[String]) {

    val typ = EnumType(name)

    /** The symbols. */
    val values: Seq[EnumValue] = valueNames.map(value => EnumValue(name, value))

    val valueMap: Map[String, EnumValue] = values.map(symbol => (symbol.valueName, symbol)).toMap

    def asJson: JsObject = Json.obj(
      "typ" -> typ.asJson,
      "values" -> values.map(_.asJson),
      "valueMap" -> valueMap.mapValues(_.asJson)
    )
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
