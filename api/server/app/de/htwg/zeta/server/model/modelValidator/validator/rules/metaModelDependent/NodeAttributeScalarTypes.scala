package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.AttributeType
import models.modelDefinitions.metaModel.elements.AttributeValue
import models.modelDefinitions.metaModel.elements.ScalarBoolType
import models.modelDefinitions.metaModel.elements.ScalarBoolValue
import models.modelDefinitions.metaModel.elements.ScalarDoubleType
import models.modelDefinitions.metaModel.elements.ScalarDoubleValue
import models.modelDefinitions.metaModel.elements.ScalarIntType
import models.modelDefinitions.metaModel.elements.ScalarIntValue
import models.modelDefinitions.metaModel.elements.ScalarStringType
import models.modelDefinitions.metaModel.elements.ScalarStringValue
import models.modelDefinitions.model.elements.Node

class NodeAttributeScalarTypes(nodeType: String, attributeType: String, attributeDataType: AttributeType) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String =
    s"Attributes of type $attributeType in nodes of type $nodeType must be of data type ${Util.getAttributeTypeClassName(attributeDataType)}."
  override val possibleFix: String =
    s"Remove attribute values of attribute $attributeType in node $nodeType which are not of data type ${Util.getAttributeTypeClassName(attributeDataType)}."

  override def isValid(node: Node): Option[Boolean] = if (node.clazz.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = {

    def handleStrings(values: Seq[AttributeValue]): Boolean = values.collect { case v: ScalarStringValue => v }.forall(_.attributeType == attributeDataType)
    def handleBooleans(values: Seq[AttributeValue]): Boolean = values.collect { case v: ScalarBoolValue => v }.forall(_.attributeType == attributeDataType)
    def handleInts(values: Seq[AttributeValue]): Boolean = values.collect { case v: ScalarIntValue => v }.forall(_.attributeType == attributeDataType)
    def handleDoubles(values: Seq[AttributeValue]): Boolean = values.collect { case v: ScalarDoubleValue => v }.forall(_.attributeType == attributeDataType)

    node.attributes.get(attributeType) match {
      case None => true
      case Some(attribute) => attribute.headOption match {
        case None => true
        case Some(head) => head match {
          case _: ScalarStringValue => handleStrings(attribute)
          case _: ScalarBoolValue => handleBooleans(attribute)
          case _: ScalarIntValue => handleInts(attribute)
          case _: ScalarDoubleValue => handleDoubles(attribute)
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
        .filter(att => Seq(ScalarStringType, ScalarIntType, ScalarBoolType, ScalarDoubleType).contains(att.`type`))
        .map(att => new NodeAttributeScalarTypes(currentClass.name, att.name, att.`type`))
    }

}
