package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeOutputsLowerBound
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeOutputsUpperBound

class OutputsOfNodesToEdges(nodeType: String, edgeType: String) {

  def haveUpperBound(upperBound: Int): NodeOutputsUpperBound = new NodeOutputsUpperBound(nodeType, edgeType, upperBound)

  def haveLowerBound(lowerBound: Int): NodeOutputsLowerBound = new NodeOutputsLowerBound(nodeType, edgeType, lowerBound)

}
