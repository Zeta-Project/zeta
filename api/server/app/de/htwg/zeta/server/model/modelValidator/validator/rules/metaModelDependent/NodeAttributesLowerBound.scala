package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeAttributesLowerBound(val nodeType: String, val attributeType: String, val lowerBound: Int) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType must have at least $lowerBound attributes of type $attributeType."
  override val possibleFix: String = s"Add attributes of type $attributeType to nodes of type $nodeType until there are at least $lowerBound attributes."

  override def isValid(node: Node): Option[Boolean] = if (node.`type`.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.attributes.find(_.name == attributeType) match {
    case Some(attribute) => attribute.value.size >= lowerBound
    case None => lowerBound == 0
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inNodes "$nodeType" haveLowerBound $lowerBound"""
}

object NodeAttributesLowerBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritAttributes(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      acc ++ currentClass.attributes.map(attr => new NodeAttributesLowerBound(currentClass.name, attr.name, attr.lowerBound))
    }
}
