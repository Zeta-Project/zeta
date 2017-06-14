package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.ScalarType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.ScalarValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.ScalarValue.MDouble
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.ScalarValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.ScalarValue.MString
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeAttributeScalarTypes(val nodeType: String, val attributeType: String, val attributeDataType: AttributeType) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String =
    s"Attributes of type $attributeType in nodes of type $nodeType must be of data type ${Util.getAttributeTypeClassName(attributeDataType)}."
  override val possibleFix: String =
    s"Remove attribute values of attribute $attributeType in node $nodeType which are not of data type ${Util.getAttributeTypeClassName(attributeDataType)}."

  override def isValid(node: Node): Option[Boolean] = if (node.`type`.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = {

    def handleStrings(values: Seq[AttributeValue]): Boolean = values.collect { case v: MString => v }.forall(_.attributeType == attributeDataType)
    def handleBooleans(values: Seq[AttributeValue]): Boolean = values.collect { case v: MBool => v }.forall(_.attributeType == attributeDataType)
    def handleInts(values: Seq[AttributeValue]): Boolean = values.collect { case v: MInt => v }.forall(_.attributeType == attributeDataType)
    def handleDoubles(values: Seq[AttributeValue]): Boolean = values.collect { case v: MDouble => v }.forall(_.attributeType == attributeDataType)

    node.attributes.find(_.name == attributeType) match {
      case None => true
      case Some(attribute) => attribute.value.headOption match {
        case None => true
        case Some(head) => head match {
          case _: MString => handleStrings(attribute.value)
          case _: MBool => handleBooleans(attribute.value)
          case _: MInt => handleInts(attribute.value)
          case _: MDouble => handleDoubles(attribute.value)
          case _ => true
        }
      }
    }
  }

  override val dslStatement: String =
    s"""Attributes ofType "$attributeType" inNodes "$nodeType" areOfScalarType "${Util.getAttributeTypeClassName(attributeDataType)}""""
}

object NodeAttributeScalarTypes extends GeneratorRule {

  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritAttributes(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      acc ++ currentClass.attributes
        .filter(att => Seq(ScalarType.String, ScalarType.Int, ScalarType.Bool, ScalarType.Double).contains(att.`type`))
        .map(att => new NodeAttributeScalarTypes(currentClass.name, att.name, att.`type`))
    }

}
