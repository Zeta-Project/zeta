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
class NodeOutputEdges(val nodeType: String, val outputTypes: Seq[String]) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String =
    s"Nodes of type $nodeType are only allowed to have output edges of types ${outputTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String =
    s"Remove all output edges that are not of types ${outputTypes.mkString("{", ", ", "}")} from nodes of type $nodeType."

  override def isValid(node: Node): Option[Boolean] = if (node.className == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.outputEdgeNames.foldLeft(true) { (acc, outputName) =>
    if (outputTypes.contains(outputName)) acc else false
  }

  override val dslStatement: String = s"""Outputs ofNodes "$nodeType" areOfTypes ${Util.stringSeqToSeqString(outputTypes)}"""
}

object NodeOutputEdges extends GeneratorRule {
  override def generateFor(concept: Concept): Seq[DslRule] = Util.inheritOutputs(concept.classes)
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      if (currentClass.outputReferenceNames.isEmpty) acc else acc :+ new NodeOutputEdges(currentClass.name, currentClass.outputReferenceNames)
    }
}
