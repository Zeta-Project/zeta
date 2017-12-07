package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType

sealed trait AttributeValue

object AttributeValue {

  /** A mixin that offers the attributes field */
  trait HasAttributeValues {

    val attributeValues: Map[String, AttributeValue]

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
  case class EnumValue(enumName: String, valueName: String) extends AttributeValue

}
