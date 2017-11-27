package de.htwg.zeta.common.format.metaModel

import scala.collection.immutable.Seq

import de.htwg.zeta.common.format.metaModel.AttributeValueFormat.sBoolean
import de.htwg.zeta.common.format.metaModel.AttributeValueFormat.sDouble
import de.htwg.zeta.common.format.metaModel.AttributeValueFormat.sEnum
import de.htwg.zeta.common.format.metaModel.AttributeValueFormat.sEnumName
import de.htwg.zeta.common.format.metaModel.AttributeValueFormat.sInt
import de.htwg.zeta.common.format.metaModel.AttributeValueFormat.sString
import de.htwg.zeta.common.format.metaModel.AttributeValueFormat.sType
import de.htwg.zeta.common.format.metaModel.AttributeValueFormat.sValue
import de.htwg.zeta.common.format.metaModel.AttributeValueFormat.sValueName
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import play.api.libs.json.JsError
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.OWrites
import play.api.libs.json.Reads


object AttributeValueFormat extends OWrites[AttributeValue] {

  private val sType = "type"
  private val sString = "string"
  private val sValue = "value"
  private val sBoolean = "boolean"
  private val sInt = "int"
  private val sDouble = "double"
  private val sEnum = "enum"
  private val sEnumName = "enumName"
  private val sValueName = "valueName"

  override def writes(attributeValue: AttributeValue): JsObject = {
    attributeValue match {
      case StringValue(value) => Json.obj(sType -> sString, sValue -> value)
      case BoolValue(value) => Json.obj(sType -> sBoolean, sValue -> value)
      case IntValue(value) => Json.obj(sType -> sInt, sValue -> value)
      case DoubleValue(value) => Json.obj(sType -> sDouble, sValue -> value)
      case EnumValue(valueName, enumName) => Json.obj(sType -> sEnum, sEnumName -> enumName, sValueName -> valueName)
    }
  }

}

class AttributeValueFormat(enums: Seq[MEnum]) extends Reads[AttributeValue] {

  override def reads(json: JsValue): JsResult[AttributeValue] = {
    (json \ sType).validate[String].flatMap {
      case `sString` => (json \ sValue).validate[String].map(StringValue)
      case `sBoolean` => (json \ sValue).validate[Boolean].map(BoolValue)
      case `sInt` => (json \ sValue).validate[Int].map(IntValue)
      case `sDouble` => (json \ sValue).validate[Double].map(DoubleValue)
      case `sEnum` => readsEnumValue(json)
    }
  }

  private def readsEnumValue(json: JsValue): JsResult[EnumValue] = {
    (json \ sEnumName).validate[String].flatMap { enumName =>
      enums.find(_.name == enumName) match {
        case Some(enum) =>
          (json \ sValueName).validate[String].flatMap { valueName =>
            enum.values.find(_.name == valueName) match {
              case Some(enumValue) => JsSuccess(enumValue)
              case None => JsError(s"Read value $valueName from MEnum $enumName, but it's not defined")
            }
          }
        case None => JsError(s"Read value from MEnum $enumName, but MEnum $enumName is not defined")
      }
    }
  }

}
