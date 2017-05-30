package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleNodeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.{AttributeType, ScalarType}
import models.modelDefinitions.metaModel.elements.ScalarValue.{MBool, MDouble, MInt, MString}
import models.modelDefinitions.model.elements.Node

class D24_NodeAttributeScalarTypes(nodeType: String, attributeType: String, attributeDataType: AttributeType) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attributes of type $attributeType in nodes of type $nodeType must be of data type ${Util.getAttributeTypeClassName(attributeDataType)}."
  override val possibleFix: String = s"Remove attribute values of attribute $attributeType in node $nodeType which are not of data type ${Util.getAttributeTypeClassName(attributeDataType)}."

  override def isValid(node: Node): Option[Boolean] = if (node.`type`.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.attributes.find(_.name == attributeType) match {
    case None => true
    case Some(attribute) => attribute.value.headOption match {
      case None => true
      case Some(head) => head match {
        case _: MString => attribute.value.collect { case v: MString => v }.forall(_.attributeType == attributeDataType)
        case _: MBool => attribute.value.collect { case v: MBool => v }.forall(_.attributeType == attributeDataType)
        case _: MInt => attribute.value.collect { case v: MInt => v }.forall(_.attributeType == attributeDataType)
        case _: MDouble => attribute.value.collect { case v: MDouble => v }.forall(_.attributeType == attributeDataType)
        case _ => true
      }
    }
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inNodes "$nodeType" areOfScalarType "${Util.getAttributeTypeClassName(attributeDataType)}""""
}

object D24_NodeAttributeScalarTypes extends GeneratorRule {

  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritAttributes(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      acc ++ currentClass.attributes
        .filter(att => Seq(ScalarType.String, ScalarType.Int, ScalarType.Bool, ScalarType.Double).contains(att.`type`))
        .map(att => new D24_NodeAttributeScalarTypes(currentClass.name, att.name, att.`type`))
    }

}
