package models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq

import play.api.libs.json.Format
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.OFormat

sealed trait AttributeType

case object ScalarStringType extends AttributeType

case object ScalarBoolType extends AttributeType

case object ScalarIntType extends AttributeType

case object ScalarDoubleType extends AttributeType


/** The MEnum implementation
 *
 * @param name   the name of the MENum instance
 * @param values the symbols
 */
case class MEnum(
    name: String,
    values: Seq[EnumSymbol]
) extends MObject with AttributeType

object MEnum {

  implicit val playJsonFormat: OFormat[MEnum] = Json.format[MEnum]

  def buildFrom(name: String, values: Seq[String]): MEnum = {
    MEnum(name, values.map(value => EnumSymbol(value, name)))
  }

}


object AttributeType {

  /*
  private val sString = "string"

  private val sBool = "bool"

  private val sInt = "int"

  private val sDouble = "double"

  private val sEnum = "enum"

  private val sType = "type"

  implicit val playJsonFormat: Reads[AttributeType] = (JsPath \ sType).read[String].map {
    case `sString` => ScalarStringType
    case `sBool` => ScalarBoolType
    case `sInt` => ScalarIntType
    case `sDouble` => ScalarDoubleType
    case `sEnum` => ((JsPath \ "name").read[String] and (JsPath \ "values").read[Seq[String]]).tupled.map(x => MEnum.buildFrom(x._1, x._2))
  } */

  implicit val playJsonFormat = new Format[AttributeType] {

    override def writes(o: AttributeType): JsValue = null // TODO

    override def reads(json: JsValue): JsResult[AttributeType] = null // TODO

  }

}
