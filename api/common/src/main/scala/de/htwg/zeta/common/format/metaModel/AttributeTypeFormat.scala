package de.htwg.zeta.common.format.metaModel

import scala.collection.immutable.Seq

import de.htwg.zeta.common.format.metaModel.AttributeTypeFormat.sBoolean
import de.htwg.zeta.common.format.metaModel.AttributeTypeFormat.sDouble
import de.htwg.zeta.common.format.metaModel.AttributeTypeFormat.sEnum
import de.htwg.zeta.common.format.metaModel.AttributeTypeFormat.sInt
import de.htwg.zeta.common.format.metaModel.AttributeTypeFormat.sName
import de.htwg.zeta.common.format.metaModel.AttributeTypeFormat.sString
import de.htwg.zeta.common.format.metaModel.AttributeTypeFormat.sUnit
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.UnitType
import play.api.libs.json.JsError
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes


object AttributeTypeFormat extends Writes[AttributeType] {

  private val sType = "type"
  private val sEnum = "enum"
  private val sName = "name"
  private val sString = "string"
  private val sDouble = "double"
  private val sInt = "int"
  private val sBoolean = "boolean"
  private val sUnit = "unit"


  val writeMEnum: Writes[MEnum] = Json.format[MEnum]

  val readMEnum: Reads[MEnum] = Json.format[MEnum]


  override def writes(typ: AttributeType): JsValue = {
    typ match {
      case StringType => JsString(StringType.asString)
      case BoolType => JsString(BoolType.asString)
      case IntType => JsString(IntType.asString)
      case DoubleType => JsString(DoubleType.asString)
      case UnitType => JsString(UnitType.asString)
      case enum: MEnum => Json.obj(
        sType -> sEnum,
        sName -> enum.name
      )
    }
  }

}

case class AttributeTypeFormat(enums: Seq[MEnum]) extends Reads[AttributeType] {

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
      case "Bool" => JsSuccess(BoolType) // TODO frontend should always send "Boolean" instead of "Bool"
      case `sInt` => JsSuccess(IntType)
      case `sDouble` => JsSuccess(DoubleType)
      case `sUnit` => JsSuccess(UnitType)
      case _ => JsError(s"Reading AttributeType ${json.value} from JsString failed")
    }
  }

  private def readsJsObject(json: JsObject): JsResult[AttributeType] = {
    (json \ AttributeTypeFormat.sType).validate[String].flatMap {
      case `sEnum` => readsMEnum(json)
      case _ => JsError(s"Reading AttributeType ${json.value} from JsObject failed")
    }
  }

  private def readsMEnum(json: JsObject): JsResult[AttributeType] = {
    (json \ sName).validate[String].flatMap { name =>
      enums.find(_.name == name) match {
        case Some(enum) => JsSuccess(enum)
        case None => JsError(s"Read MEnum $name, but it's not defined")
      }
    }
  }

}
