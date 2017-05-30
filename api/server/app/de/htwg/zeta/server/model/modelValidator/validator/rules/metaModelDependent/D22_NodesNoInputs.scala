package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleNodeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Node

class D22_NodesNoInputs(nodeType: String) extends SingleNodeRule with DslRule {

  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType must not have input edges."
  override val possibleFix: String = s"Remove all input edges from node of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.`type`.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.inputs.flatten(_.edges).isEmpty

  override val dslStatement: String = s"""Nodes ofType "$nodeType" haveNoInputs ()"""
}

object D22_NodesNoInputs extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritInputs(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .filter(_.inputs.isEmpty)
    .map(cl => new D22_NodesNoInputs(cl.name))
}
