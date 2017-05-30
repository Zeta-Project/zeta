package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleNodeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Node

class D14_NodeOutputsLowerBound(nodeType: String, outputType: String, lowerBound: Int) extends SingleNodeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Nodes of type $nodeType must have at least $lowerBound output edges of type $outputType."
  override val possibleFix: String = s"Add output edges of type $outputType to nodes of type $nodeType until there are at least $lowerBound output edges."

  override def isValid(node: Node): Option[Boolean] = if (node.`type`.name == nodeType) Some(rule(node)) else None

  def rule(node: Node): Boolean = node.outputs.find(_.`type`.name == outputType) match {
    case Some(output) => output.edges.size >= lowerBound
    case None => lowerBound == 0
  }

  override val dslStatement: String = s"""Outputs ofNodes "$nodeType" toEdges "$outputType" haveLowerBound $lowerBound"""
}

object D14_NodeOutputsLowerBound extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.inheritOutputs(Util.simplifyMetaModelGraph(metaModel))
    .filterNot(_.abstractness)
    .foldLeft(Seq[DslRule]()) { (acc, currentClass) =>
      acc ++ currentClass.outputs.map(output => new D14_NodeOutputsLowerBound(currentClass.name, output.name, output.lowerBound))
    }
}
