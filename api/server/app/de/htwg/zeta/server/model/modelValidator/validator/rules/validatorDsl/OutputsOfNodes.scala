package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeOutputEdges

class OutputsOfNodes(nodeType: String) {

  def areOfTypes(outputTypes: Seq[String]): NodeOutputEdges = new NodeOutputEdges(nodeType, outputTypes)

  def toEdges(edgeType: String): OutputsOfNodesToEdges = new OutputsOfNodesToEdges(nodeType, edgeType)

}
