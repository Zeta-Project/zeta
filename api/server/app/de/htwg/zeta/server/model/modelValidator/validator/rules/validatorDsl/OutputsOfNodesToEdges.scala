package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeOutputsUpperBound
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeOutputsLowerBound

class OutputsOfNodesToEdges(nodeType: String, edgeType: String) {

  def haveUpperBound(upperBound: Int) = new NodeOutputsUpperBound(nodeType, edgeType, upperBound)

  def haveLowerBound(lowerBound: Int) = new NodeOutputsLowerBound(nodeType, edgeType, lowerBound)

}
