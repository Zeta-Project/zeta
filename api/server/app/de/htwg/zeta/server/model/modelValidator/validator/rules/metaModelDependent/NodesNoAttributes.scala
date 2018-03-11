package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.concept.Concept
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodesNoAttributes(val nodeType: String) extends SingleNodeRule with DslRule {

  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType must not have attributes."
  override val possibleFix: String = s"Remote all attributes from nodes of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.className == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.attributeValues.values.isEmpty

  override val dslStatement: String = s"""Nodes ofType "$nodeType" haveNoAttributes ()"""
}

object NodesNoAttributes extends GeneratorRule {
  override def generateFor(concept: Concept): Seq[DslRule] = Util.inheritAttributes(concept.classes)
    .filterNot(_.abstractness)
    .filter(_.attributes.isEmpty)
    .map(cl => new NodesNoAttributes(cl.name))
}
