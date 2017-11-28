package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.EnumType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.UnitType
import play.api.libs.json.Format
import play.api.libs.json.JsError
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue


object AttributeTypeFormat extends Format[AttributeType] {

  private val sType = "type"
  private val sEnum = "enum"
  private val sName = "name"
  private val sString = "string"
  private val sDouble = "double"
  private val sInt = "int"
  private val sBoolean = "boolean"
  private val sUnit = "unit"

  override def writes(typ: AttributeType): JsValue = {
    typ match {
      case StringType => JsString(sString)
      case BoolType => JsString(sBoolean)
      case IntType => JsString(sInt)
      case DoubleType => JsString(sDouble)
      case UnitType => JsString(sUnit)
      case enum: MEnum => Json.obj(
        sType -> sEnum,
        sName -> enum.name
      )
    }
  }

  override def reads(json: JsValue): JsResult[AttributeType] = {
    json match {
      case s: JsString => readsJsString(s)
      case o: JsObject => readsJsObject(o)
      case _ => JsError(s"Reading AttributeType $json failed")
    }
  }

  private def readsJsString(json: JsString): JsResult[AttributeType] = {
    json.value match {
      case `sString` => JsSuccess(StringType)
      case `sBoolean` => JsSuccess(BoolType)
      case `sInt` => JsSuccess(IntType)
      case `sDouble` => JsSuccess(DoubleType)
      case `sUnit` => JsSuccess(UnitType)
      case _ => JsError(s"Reading AttributeType ${json.value} from JsString failed")
    }
  }

  private def readsJsObject(json: JsObject): JsResult[AttributeType] = {
    (json \ AttributeTypeFormat.sType).validate[String].flatMap {
      case `sEnum` => (json \sName).validate[String].map(EnumType)
      case _ => JsError(s"Reading AttributeType ${json.value} from JsObject failed")
    }
  }

}
