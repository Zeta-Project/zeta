package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeTargetNodes

class TargetsOfEdges(edgeType: String) {

  def areOfTypes(targetTypes: Seq[String]): EdgeTargetNodes = new EdgeTargetNodes(edgeType, targetTypes)

  def toNodes(nodeType: String): TargetsOfEdgesToNodes = new TargetsOfEdgesToNodes(edgeType, nodeType)

}
