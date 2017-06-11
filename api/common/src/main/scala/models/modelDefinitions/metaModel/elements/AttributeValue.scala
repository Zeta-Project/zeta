package models.modelDefinitions.metaModel.elements

import play.api.libs.json.Format
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

sealed trait AttributeValue

object AttributeValue {

  implicit val playJsonFormat = new Format[AttributeValue] {
    override def writes(o: AttributeValue): JsValue = null // TODO

    override def reads(json: JsValue): JsResult[AttributeValue] = null // TODO
  }

}

case class ScalarStringValue(value: String) extends AttributeValue {
  val attributeType = ScalarStringType
}

case class ScalarBoolValue(value: Boolean) extends AttributeValue {
  val attributeType = ScalarBoolType
}

case class ScalarIntValue(value: Int) extends AttributeValue {
  val attributeType = ScalarIntType
}

case class ScalarDoubleValue(value: Double) extends AttributeValue {
  val attributeType = ScalarDoubleType
}

/** An Enum Symbol
 *
 * @param name     name of the symbol
 * @param enumName name of the the belonging MEnum
 */
case class EnumSymbol(name: String, enumName: String) extends AttributeValue

object EnumSymbol {

  implicit val playJsonFormat: OFormat[EnumSymbol] = Json.format[EnumSymbol]

}
