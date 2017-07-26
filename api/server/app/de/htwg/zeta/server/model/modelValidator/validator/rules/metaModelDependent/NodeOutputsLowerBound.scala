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
class NodeOutputsLowerBound(val nodeType: String, val outputType: String, val lowerBound: Int) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType must have at least $lowerBound output edges of type $outputType."
  override val possibleFix: String = s"Add output edges of type $outputType to nodes of type $nodeType until there are at least $lowerBound output edges."

  override def isValid(node: Node): Option[Boolean] = if (node.className == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.outputs.find(_.referenceName == outputType) match {
    case Some(output) => output.edgeIds.size >= lowerBound
    case None => lowerBound == 0
  }

  override val dslStatement: String = s"""Outputs ofNodes "$nodeType" toEdges "$outputType" haveLowerBound $lowerBound"""
}

object NodeOutputsLowerBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritOutputs(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      acc ++ currentClass.outputs.map(output => new NodeOutputsLowerBound(currentClass.name, output.name, output.lowerBound))
    }
}
