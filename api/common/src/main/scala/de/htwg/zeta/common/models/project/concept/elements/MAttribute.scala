package de.htwg.zeta.common.models.project.concept.elements

import scala.collection.immutable.Seq

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
    transient: Boolean
)

object MAttribute {

  trait AttributeMap {

    val attributes: Seq[MAttribute]

    /** Attributes mapped to their own names. */
    final val attributeMap: Map[String, MAttribute] = attributes.map(attribute => (attribute.name, attribute)).toMap

  }

}
