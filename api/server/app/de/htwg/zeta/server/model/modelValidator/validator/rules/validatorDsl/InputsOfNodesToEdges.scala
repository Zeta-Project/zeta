package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeInputsUpperBound
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeInputsLowerBound

class InputsOfNodesToEdges(nodeType: String, edgeType: String) {

  def haveUpperBound(upperBound: Int) = new NodeInputsUpperBound(nodeType, edgeType, upperBound)

  def haveLowerBound(lowerBound: Int) = new NodeInputsLowerBound(nodeType, edgeType, lowerBound)

}
