package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.{D15_NodeInputsUpperBound, D16_NodeInputsLowerBound}

class InputsOfNodesToEdges(nodeType: String, edgeType: String) {

  def haveUpperBound(upperBound: Int) = new D15_NodeInputsUpperBound(nodeType, edgeType, upperBound)

  def haveLowerBound(lowerBound: Int) = new D16_NodeInputsLowerBound(nodeType, edgeType, lowerBound)

}
