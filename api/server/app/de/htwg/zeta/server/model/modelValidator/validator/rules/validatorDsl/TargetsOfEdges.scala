package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeTargetNodes

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class TargetsOfEdges(edgeType: String) {

  def areOfTypes(targetTypes: Seq[String]): EdgeTargetNodes = new EdgeTargetNodes(edgeType, targetTypes)

  def toNodes(nodeType: String): TargetsOfEdgesToNodes = new TargetsOfEdgesToNodes(edgeType, nodeType)

}
