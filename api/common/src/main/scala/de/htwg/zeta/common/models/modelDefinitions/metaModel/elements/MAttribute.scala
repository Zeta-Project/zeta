package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import play.api.libs.json.Format
import play.api.libs.json.Json

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

  implicit val playJsonFormat: Format[MAttribute] = Json.format[MAttribute]

}
