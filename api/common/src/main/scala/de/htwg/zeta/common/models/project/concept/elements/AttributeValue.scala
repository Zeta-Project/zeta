package de.htwg.zeta.common.models.project.concept.elements

import de.htwg.zeta.common.models.project.concept.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.EnumType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.IntType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.StringType
import play.api.libs.json.JsObject
import play.api.libs.json.Json

sealed trait AttributeValue{
  val attributeType: AttributeType

  def asJson: JsObject = Json.obj("attributeType" -> attributeType.asJson)
}

object AttributeValue {

  /** A mixin that offers the attributes field */
  trait HasAttributeValues {

    val attributeValues: Map[String, List[AttributeValue]]

  }

  case class StringValue(value: String) extends AttributeValue {
    val attributeType: AttributeType = StringType
  }

  case class BoolValue(value: Boolean) extends AttributeValue {
    val attributeType: AttributeType = BoolType
  }

  case class IntValue(value: Int) extends AttributeValue {
    val attributeType: AttributeType = IntType
  }

  case class DoubleValue(value: Double) extends AttributeValue {
    val attributeType: AttributeType = DoubleType
  }

  /** An Enum Symbol
   *
   * @param enumName name of the the belonging MEnum
   * @param valueName     name of the symbol
   */
  case class EnumValue(enumName: String, valueName: String) extends AttributeValue {
    val attributeType: AttributeType = AttributeType.EnumType(enumName)
  }

}
