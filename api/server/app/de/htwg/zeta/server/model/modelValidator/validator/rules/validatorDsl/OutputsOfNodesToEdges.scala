package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.{D13_NodeOutputsUpperBound, D14_NodeOutputsLowerBound}

class OutputsOfNodesToEdges(nodeType: String, edgeType: String) {

  def haveUpperBound(upperBound: Int) = new D13_NodeOutputsUpperBound(nodeType, edgeType, upperBound)

  def haveLowerBound(lowerBound: Int) = new D14_NodeOutputsLowerBound(nodeType, edgeType, lowerBound)

}
