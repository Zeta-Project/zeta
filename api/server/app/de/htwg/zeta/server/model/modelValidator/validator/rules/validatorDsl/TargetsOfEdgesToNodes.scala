package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.{D07_EdgeTargetsUpperBound, D08_EdgeTargetsLowerBound}

class TargetsOfEdgesToNodes(edgeType: String, nodeType: String) {

  def haveUpperBound(upperBound: Int) = new D07_EdgeTargetsUpperBound(edgeType, nodeType, upperBound)

  def haveLowerBound(lowerBound: Int) = new D08_EdgeTargetsLowerBound(edgeType, nodeType, lowerBound)

}
