package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeTargetsLowerBound
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeTargetsUpperBound

class TargetsOfEdgesToNodes(edgeType: String, nodeType: String) {

  def haveUpperBound(upperBound: Int): EdgeTargetsUpperBound = new EdgeTargetsUpperBound(edgeType, nodeType, upperBound)

  def haveLowerBound(lowerBound: Int): EdgeTargetsLowerBound = new EdgeTargetsLowerBound(edgeType, nodeType, lowerBound)

}
