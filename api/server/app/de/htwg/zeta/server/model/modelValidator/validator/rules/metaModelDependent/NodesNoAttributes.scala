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
class NodesNoAttributes(val nodeType: String) extends SingleNodeRule with DslRule {

  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType must not have attributes."
  override val possibleFix: String = s"Remote all attributes from nodes of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.`type`.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.attributes.flatten(_.value).isEmpty

  override val dslStatement: String = s"""Nodes ofType "$nodeType" haveNoAttributes ()"""
}

object NodesNoAttributes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritAttributes(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .filter(_.attributes.isEmpty)
    .map(cl => new NodesNoAttributes(cl.name))
}
