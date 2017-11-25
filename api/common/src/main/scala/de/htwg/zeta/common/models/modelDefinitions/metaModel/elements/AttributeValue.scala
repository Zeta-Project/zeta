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

  /** A mixin that offers the attributes field */
  trait HasAttributeValues {

    val attributeValues: Map[String, Seq[AttributeValue]]

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
   * @param name     name of the symbol
   * @param enumName name of the the belonging MEnum
   */
  case class EnumValue(enumName: String, name: String) extends AttributeValue

}
