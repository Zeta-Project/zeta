package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeSourcesUpperBound
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeSourcesLowerBound

class SourcesOfEdgesToNodes(edgeType: String, nodeType: String) {

  def haveUpperBound(upperBound: Int) = new EdgeSourcesUpperBound(edgeType, nodeType, upperBound)

  def haveLowerBound(lowerBound: Int) = new EdgeSourcesLowerBound(edgeType, nodeType, lowerBound)

}
