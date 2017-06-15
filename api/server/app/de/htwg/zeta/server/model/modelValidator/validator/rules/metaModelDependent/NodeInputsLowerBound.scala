package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleNodeRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Node

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeInputsLowerBound(val nodeType: String, val inputType: String, val lowerBound: Int) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType must have at least $lowerBound input edges of type $inputType."
  override val possibleFix: String = s"Add input edges of type $inputType to nodes of type $nodeType until there are at least $lowerBound input edges."

  override def isValid(node: Node): Option[Boolean] = if (node.clazz.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.inputs.find(_.reference.name == inputType) match {
    case Some(input) => input.edgeNames.size >= lowerBound
    case None => lowerBound == 0
  }

  override val dslStatement: String = s"""Inputs ofNodes "$nodeType" toEdges "$inputType" haveLowerBound $lowerBound"""
}

object NodeInputsLowerBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritInputs(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      acc ++ currentClass.inputs.map(input => new NodeInputsLowerBound(currentClass.name, input.name, input.lowerBound))
    }
}
