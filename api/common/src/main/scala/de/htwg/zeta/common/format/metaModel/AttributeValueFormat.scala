package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class AttributeValueFormat(
    sType: String = "type",
    sString: String = "string",
    sValue: String = "value",
    sBoolean: String = "boolean",
    sInt: String = "int",
    sDouble: String = "double",
    sEnum: String = "enum",
    sEnumName: String = "enumName",
    sValueName: String = "valueName"
) extends OFormat[AttributeValue] {

  override def writes(attributeValue: AttributeValue): JsObject = {
    attributeValue match {
      case StringValue(value) => Json.obj(sType -> sString, sValue -> value)
      case BoolValue(value) => Json.obj(sType -> sBoolean, sValue -> value)
      case IntValue(value) => Json.obj(sType -> sInt, sValue -> value)
      case DoubleValue(value) => Json.obj(sType -> sDouble, sValue -> value)
      case EnumValue(enumName, valueName) => Json.obj(sType -> sEnum, sEnumName -> enumName, sValueName -> valueName)
    }
  }

  override def reads(json: JsValue): JsResult[AttributeValue] = {
    (json \ sType).validate[String].flatMap {
      case `sString` => (json \ sValue).validate[String].map(StringValue)
      case `sBoolean` => (json \ sValue).validate[Boolean].map(BoolValue)
      case `sInt` => (json \ sValue).validate[Int].map(IntValue)
      case `sDouble` => (json \ sValue).validate[Double].map(DoubleValue)
      case `sEnum` => readsEnumValue(json)
    }
  }

  private def readsEnumValue(json: JsValue): JsResult[EnumValue] = for {
    enumName <- (json \ sEnumName).validate[String]
    enumValue <- (json \ sValueName).validate[String]
  } yield {
    EnumValue(enumName, enumValue)
  }

}
