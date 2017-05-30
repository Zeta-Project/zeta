package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.D11_NodeInputEdges

class InputsOfNodes(nodeType: String) {

  def areOfTypes(inputTypes: Seq[String]) = new D11_NodeInputEdges(nodeType, inputTypes)

  def toEdges(edgeType: String) = new InputsOfNodesToEdges(nodeType, edgeType)

}
