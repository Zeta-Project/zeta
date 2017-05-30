package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleNodeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Node

class D02_NodeAttributes(nodeType: String, attributeTypes: Seq[String]) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType are only allowed to have attributes of types ${attributeTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String = s"Remove all attributes that are not of types ${attributeTypes.mkString("{", ", ", "}")} from nodes of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.`type`.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.attributes.map(_.name).foldLeft(true) { (acc, attributeName) =>
    if (attributeTypes.contains(attributeName)) acc else false
  }

  override val dslStatement: String = s"""Attributes inNodes "$nodeType" areOfTypes ${Util.stringSeqToSeqString(attributeTypes)}"""
}

object D02_NodeAttributes extends GeneratorRule {

  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritAttributes(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      if (currentClass.attributes.isEmpty) acc
      else acc :+ new D02_NodeAttributes(currentClass.name, currentClass.attributes.map(_.name))
    }

}
