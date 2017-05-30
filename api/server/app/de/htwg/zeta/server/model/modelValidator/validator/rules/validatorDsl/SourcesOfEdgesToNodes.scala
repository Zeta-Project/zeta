package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.{D09_EdgeSourcesUpperBound, D10_EdgeSourcesLowerBound}

class SourcesOfEdgesToNodes(edgeType: String, nodeType: String) {

  def haveUpperBound(upperBound: Int) = new D09_EdgeSourcesUpperBound(edgeType, nodeType, upperBound)

  def haveLowerBound(lowerBound: Int) = new D10_EdgeSourcesLowerBound(edgeType, nodeType, lowerBound)

}
