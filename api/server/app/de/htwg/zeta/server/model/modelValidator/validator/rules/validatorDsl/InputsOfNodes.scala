package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeInputEdges

class InputsOfNodes(nodeType: String) {

  def areOfTypes(inputTypes: Seq[String]) = new NodeInputEdges(nodeType, inputTypes)

  def toEdges(edgeType: String) = new InputsOfNodesToEdges(nodeType, edgeType)

}
