package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeInputsLowerBound
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeInputsUpperBound

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class InputsOfNodesToEdges(nodeType: String, edgeType: String) {

  def haveUpperBound(upperBound: Int): NodeInputsUpperBound = new NodeInputsUpperBound(nodeType, edgeType, upperBound)

  def haveLowerBound(lowerBound: Int): NodeInputsLowerBound = new NodeInputsLowerBound(nodeType, edgeType, lowerBound)

}
