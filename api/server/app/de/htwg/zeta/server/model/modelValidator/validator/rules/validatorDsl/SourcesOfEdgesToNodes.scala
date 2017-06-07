package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeSourcesLowerBound
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeSourcesUpperBound

class SourcesOfEdgesToNodes(edgeType: String, nodeType: String) {

  def haveUpperBound(upperBound: Int): EdgeSourcesUpperBound = new EdgeSourcesUpperBound(edgeType, nodeType, upperBound)

  def haveLowerBound(lowerBound: Int): EdgeSourcesLowerBound = new EdgeSourcesLowerBound(edgeType, nodeType, lowerBound)

}
