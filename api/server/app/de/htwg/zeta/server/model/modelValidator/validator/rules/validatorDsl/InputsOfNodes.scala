package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeInputEdges

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class InputsOfNodes(nodeType: String) {

  def areOfTypes(inputTypes: Seq[String]): NodeInputEdges = new NodeInputEdges(nodeType, inputTypes)

}
