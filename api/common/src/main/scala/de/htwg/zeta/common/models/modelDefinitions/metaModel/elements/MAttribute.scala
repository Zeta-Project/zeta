package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.collection.immutable.Seq
import scala.util.Try

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import play.api.libs.json.JsBoolean
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes

/**
 * The MAttribute implementation
 *
 * @param name             the name of the MAttribute instance
 * @param globalUnique     globalUnique flag
 * @param localUnique      localUnique flag
 * @param typ              the attribute type
 * @param default          the attribute's default value
 * @param constant         constant flag
 * @param singleAssignment single assignment flag
 * @param expression       a composed expression
 * @param ordered          ordered flag
 * @param transient        transient flag
 * @param upperBound       the upper bound
 * @param lowerBound       the lower bound
 */
case class MAttribute(
    name: String,
    globalUnique: Boolean,
    localUnique: Boolean,
    typ: AttributeType,
    default: AttributeValue,
    constant: Boolean,
    singleAssignment: Boolean,
    expression: String,
    ordered: Boolean,
    transient: Boolean,
    upperBound: Int,
    lowerBound: Int)
  extends MObject with MBounds

object MAttribute {

  trait HasAttributes {

    val attributes: Seq[MAttribute]

    /** Attributes mapped to their own names. */
    final val attributeMap: Map[String, MAttribute] = attributes.map(attribute => (attribute.name, attribute)).toMap

  }

  private val sName = "name"
  private val sGlobalUnique = "globalUnique"
  private val sLocalUnique = "localUnique"
  private val sTyp = "typ"
  private val sDefault = "default"
  private val sConstant = "constant"
  private val sSingleAssignment = "singleAssignment"
  private val sExpression = "expression"
  private val sOrdered = "ordered"
  private val sTransient = "transient"
  private val sUpperBound = "upperBound"
  private val sLowerBound = "lowerBound"

  def playJsonReads(enums: Seq[MEnum]): Reads[MAttribute] = new Reads[MAttribute] {

    override def reads(json: JsValue): JsResult[MAttribute] = {
      for {
        name <- (json \ sName).validate[String]
        globalUnique <- (json \ sGlobalUnique).validate[Boolean]
        localUnique <- (json \ sLocalUnique).validate[Boolean]
        typ <- (json \ sTyp).validate(AttributeType.playJsonReads(enums))
        default <- typ match {
          case StringType => (json \ sDefault).validate[String].map(StringValue)
          case BoolType => (json \ sDefault).validate[String].map(v => BoolValue(v.toBoolean))
          case IntType => (json \ sDefault).validate[String].map(v => IntValue(v.toInt))
          case DoubleType => (json \ sDefault).validate[String].map(v => DoubleValue(v.toDouble))
          case enum: MEnum => (json \ sDefault).validate[String].map(enum.valueMap(_))
        }
        constant <- (json \ sConstant).validate[Boolean]
        singleAssignment <- (json \ sSingleAssignment).validate[Boolean]
        expression <- (json \ sExpression).validate[String]
        ordered <- (json \ sOrdered).validate[Boolean]
        transient <- (json \ sTransient).validate[Boolean]
        upperBound <- (json \ sUpperBound).validate[Int]
        lowerBound <- (json \ sLowerBound).validate[Int]
      } yield {
        MAttribute(
          name = name,
          globalUnique = globalUnique,
          localUnique = localUnique,
          typ = typ,
          default = default,
          constant = constant,
          singleAssignment = singleAssignment,
          expression = expression,
          ordered = ordered,
          transient = transient,
          upperBound = upperBound,
          lowerBound = lowerBound
        )
      }
    }

  }

  implicit val playJsonWrites: Writes[MAttribute] = new Writes[MAttribute] {
    override def writes(attribute: MAttribute): JsValue = {
      JsObject(Map(
        sName -> JsString(attribute.name),
        sGlobalUnique -> JsBoolean(attribute.globalUnique),
        sLocalUnique -> JsBoolean(attribute.localUnique),
        sTyp -> AttributeType.playJsonWrites.writes(attribute.typ),
        sDefault -> (attribute.default match {
          case StringValue(s) => JsString(s)
          case BoolValue(b) => JsBoolean(b)
          case DoubleValue(d) => JsNumber(d)
          case IntValue(i) => JsNumber(i)
          case EnumValue(_, value) => JsString(value)
        }),
        sConstant -> JsBoolean(attribute.constant),
        sSingleAssignment -> JsBoolean(attribute.singleAssignment),
        sExpression -> JsString(attribute.expression),
        sOrdered -> JsBoolean(attribute.ordered),
        sTransient -> JsBoolean(attribute.transient),
        sUpperBound -> JsNumber(attribute.upperBound),
        sLowerBound -> JsNumber(attribute.lowerBound)
      ))
    }
  }

}
