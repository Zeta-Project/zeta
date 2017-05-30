package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleNodeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Node

class D23_NodesNoOutputs(nodeType: String) extends SingleNodeRule with DslRule {

  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType must not have output edges."
  override val possibleFix: String = s"Remove all output edges from node of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.`type`.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.outputs.flatten(_.edges).isEmpty

  override val dslStatement: String = s"""Nodes ofType "$nodeType" haveNoOutputs ()"""
}

object D23_NodesNoOutputs extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritOutputs(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .filter(_.outputs.isEmpty)
    .map(cl => new D23_NodesNoOutputs(cl.name))
}
