package de.htwg.zeta.server.controller.restApi.metaModelUiFormat

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import play.api.libs.json.JsValue
import play.api.libs.json.JsString
import play.api.libs.json.JsNumber
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsResult
import play.api.libs.json.Reads
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsError
import play.api.libs.json.Writes
import play.api.libs.json.Json

private[metaModelUiFormat] class MAttributeFormat(val enumMap: Map[String, MEnum]) extends MBoundsFormat[MAttribute] {
  private val singletonString = JsSuccess(StringType)
  private val singletonBool = JsSuccess(BoolType)
  private val singletonInt = JsSuccess(IntType)
  private val singletonDouble = JsSuccess(DoubleType)


  private val typeDefaultError = JsError("type definition and default value don't match")

  // used to validate JsLookup. JsValue has flatMap. JsLookup hasn't
  private object ToJsResult extends Reads[JsValue] {
    override def reads(json: JsValue): JsResult[JsValue] = JsSuccess(json)
  }

  private def detectType(name: String): JsResult[AttributeType] = name match {
    case "String" => singletonString
    case "Bool" => singletonBool
    case "Int" => singletonInt
    case "Double" => singletonDouble
    case _ => enumMap.get(name) match {
      case Some(enum) => JsSuccess(enum)
      case None => JsError(s"Enum with name $name should be part of the MetaModel")
    }
  }

  private object CheckValidInt {
    def unapply(jsn: JsNumber): Option[Int] = try {
      Some(jsn.value.toIntExact)
    } catch {
      case _: java.lang.ArithmeticException => None
    }
  }

  private def validateTypeDefault(typ: AttributeType, default: JsValue): JsResult[AttributeValue] = (typ, default) match {
    case (StringType, JsString(s)) => JsSuccess(AttributeValue.StringValue(s))
    case (BoolType, JsBoolean(b)) => JsSuccess(AttributeValue.BoolValue(b))
    case (DoubleType, JsNumber(n)) => JsSuccess(AttributeValue.DoubleValue(n.doubleValue))
    case (IntType, CheckValidInt(i)) => JsSuccess(AttributeValue.IntValue(i))
    case (MEnum(enumName, values), JsString(name)) if values.contains(name) => JsSuccess(AttributeValue.EnumValue(name, enumName))
    case _ => typeDefaultError
  }

  override def readsUnchecked(json: JsValue): JsResult[MAttribute] = {
    for {
      name <- json.\("name").validate[String]
      globalUnique <- json.\("globalUnique").validate[Boolean]
      localUnique <- json.\("localUnique").validate[Boolean]
      typ <- json.\("type").validate[String].flatMap(detectType)
      default <- json.\("default").validate(ToJsResult).flatMap(validateTypeDefault(typ, _))
      constant <- json.\("constant").validate[Boolean]
      singleAssignment <- json.\("singleAssignment").validate[Boolean]
      expression <- json.\("expression").validate[String]
      ordered <- json.\("ordered").validate[Boolean]
      transient <- json.\("transient").validate[Boolean]
      upperBound <- json.\("upperBound").validate[Int](Reads.min(-1))
      lowerBound <- json.\("lowerBound").validate[Int](Reads.min(0))
    } yield {
      MAttribute(name, globalUnique, localUnique, typ, default, constant, singleAssignment, expression, ordered, transient, upperBound, lowerBound)
    }
  }


  override def writes(ma: MAttribute): JsValue = MAttributeFormat.writes(ma)
}

private[metaModelUiFormat] object MAttributeFormat extends Writes[MAttribute] {

  private def writesAttributeType(a: AttributeType): JsValue = {
    val out = a match {
      case MEnum(name, _) => name
      case _ => a.asString
    }
    JsString(out)
  }

  private def writesAttributeValue(av: AttributeValue): JsValue = av match {
    case StringValue(v) => JsString(v)
    case BoolValue(v) => JsBoolean(v)
    case IntValue(v) => JsNumber(v)
    case DoubleValue(v) => JsNumber(v)
    case EnumValue(name, _) => JsString(name)
  }


  override def writes(ma: MAttribute): JsValue = {
    Json.obj(
      "name" -> ma.name,
      "globalUnique" -> ma.globalUnique,
      "localUnique" -> ma.localUnique,
      "type" -> writesAttributeType(ma.typ),
      "default" -> writesAttributeValue(ma.default),
      "constant" -> ma.constant,
      "singleAssignment" -> ma.singleAssignment,
      "expression" -> ma.expression,
      "ordered" -> ma.ordered,
      "transient" -> ma.transient,
      "upperBound" -> ma.upperBound,
      "lowerBound" -> ma.lowerBound
    )
  }
}
