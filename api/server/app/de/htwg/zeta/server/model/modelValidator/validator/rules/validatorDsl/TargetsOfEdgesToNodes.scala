package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeTargetsLowerBound
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeTargetsUpperBound

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class TargetsOfEdgesToNodes(edgeType: String, nodeType: String) {

  def haveUpperBound(upperBound: Int): EdgeTargetsUpperBound = new EdgeTargetsUpperBound(edgeType, nodeType, upperBound)

  def haveLowerBound(lowerBound: Int): EdgeTargetsLowerBound = new EdgeTargetsLowerBound(edgeType, nodeType, lowerBound)

}
