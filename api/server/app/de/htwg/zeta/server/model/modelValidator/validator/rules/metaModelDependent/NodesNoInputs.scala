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
class NodesNoInputs(val nodeType: String) extends SingleNodeRule with DslRule {

  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType must not have input edges."
  override val possibleFix: String = s"Remove all input edges from node of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.className == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.inputEdgeNames.isEmpty

  override val dslStatement: String = s"""Nodes ofType "$nodeType" haveNoInputs ()"""
}

object NodesNoInputs extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritInputs(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .filter(_.inputs.isEmpty)
    .map(cl => new NodesNoInputs(cl.name))
}
