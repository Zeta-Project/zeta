package de.htwg.zeta.common.format.metaModel

import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.EnumType
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.UnitType
import play.api.libs.json.Format
import play.api.libs.json.JsError
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class AttributeTypeFormat(
    sType: String = "type",
    sEnum: String = "enum",
    sName: String = "name",
    sString: String = "string",
    sDouble: String = "double",
    sInt: String = "int",
    sBoolean: String = "boolean",
    sUnit: String = "unit"
) extends Format[AttributeType] {

  override def writes(typ: AttributeType): JsValue = {
    typ match {
      case StringType => JsString(sString)
      case BoolType => JsString(sBoolean)
      case IntType => JsString(sInt)
      case DoubleType => JsString(sDouble)
      case UnitType => JsString(sUnit)
      case EnumType(name) => Json.obj(
        sType -> sEnum,
        sName -> name
      )
    }
  }

  override def reads(json: JsValue): JsResult[AttributeType] = {
    json match {
      case s: JsString => readsJsString(s)
      case o: JsObject => readsJsObject(o)
      case _ => JsError(s"Unknown AttributeType from JsValue: $json") // scalastyle:ignore multiple.string.literals
    }
  }

  private def readsJsString(json: JsString): JsResult[AttributeType] = {
    json.value match {
      case `sString` => JsSuccess(StringType)
      case `sBoolean` => JsSuccess(BoolType)
      case `sInt` => JsSuccess(IntType)
      case `sDouble` => JsSuccess(DoubleType)
      case `sUnit` => JsSuccess(UnitType)
      case _ => JsError(s"Unknown AttributeType from JsString: ${json.value}")
    }
  }

  private def readsJsObject(json: JsObject): JsResult[AttributeType] = {
    (json \ sType).validate[String].flatMap {
      case `sEnum` => (json \ sName).validate[String].map(EnumType)
      case _ => JsError(s"Unknown AttributeType from JsObject: ${json.value}")
    }
  }

}
