package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeOutputEdges

class OutputsOfNodes(nodeType: String) {

  def areOfTypes(outputTypes: Seq[String]) = new NodeOutputEdges(nodeType, outputTypes)

  def toEdges(edgeType: String) = new OutputsOfNodesToEdges(nodeType, edgeType)

}
