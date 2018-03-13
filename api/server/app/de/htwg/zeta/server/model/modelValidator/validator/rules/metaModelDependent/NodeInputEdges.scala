package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeInputEdges(val nodeType: String, val inputTypes: Seq[String]) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType are only allowed to have input edges of types ${inputTypes.mkString("{", ", ", "}")}."
  override val possibleFix: String = s"Remove all input edges that are not of types ${inputTypes.mkString("{", ", ", "}")} from nodes of type $nodeType."

  override def isValid(node: NodeInstance): Option[Boolean] = if (node.className == nodeType) Some(rule(node)) else None

  def rule(node: NodeInstance): Boolean = node.inputEdgeNames.foldLeft(true) { (acc, inputName) =>
    if (inputTypes.contains(inputName)) acc else false
  }

  override val dslStatement: String = s"""Inputs ofNodes "$nodeType" areOfTypes ${Util.stringSeqToSeqString(inputTypes)}"""
}

object NodeInputEdges extends GeneratorRule {
  override def generateFor(concept: Concept): Seq[DslRule] = Util.inheritInputs(concept.classes)
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      if (currentClass.inputReferenceNames.isEmpty) acc else acc :+ new NodeInputEdges(currentClass.name, currentClass.inputReferenceNames)
    }
}
