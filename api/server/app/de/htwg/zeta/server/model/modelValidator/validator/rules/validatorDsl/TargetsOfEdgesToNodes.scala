package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeTargetsUpperBound
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeTargetsLowerBound

class TargetsOfEdgesToNodes(edgeType: String, nodeType: String) {

  def haveUpperBound(upperBound: Int) = new EdgeTargetsUpperBound(edgeType, nodeType, upperBound)

  def haveLowerBound(lowerBound: Int) = new EdgeTargetsLowerBound(edgeType, nodeType, lowerBound)

}
