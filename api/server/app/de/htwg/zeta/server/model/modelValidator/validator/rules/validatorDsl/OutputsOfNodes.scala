package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeOutputEdges

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class OutputsOfNodes(nodeType: String) {

  def areOfTypes(outputTypes: Seq[String]): NodeOutputEdges = new NodeOutputEdges(nodeType, outputTypes)

}
