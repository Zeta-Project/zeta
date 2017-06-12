package models.modelDefinitions.metaModel.elements

import models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import models.modelDefinitions.metaModel.elements.AttributeType.IntType
import models.modelDefinitions.metaModel.elements.AttributeType.StringType
import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.libs.json.JsResult
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
    override def writes(o: AttributeValue): JsValue = null // TODO

    override def reads(json: JsValue): JsResult[AttributeValue] = null // TODO
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

}
