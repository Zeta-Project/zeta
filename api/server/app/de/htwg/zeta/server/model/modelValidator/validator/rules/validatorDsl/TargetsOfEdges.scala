package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.D06_EdgeTargetNodes

class TargetsOfEdges(edgeType: String) {

  def areOfTypes(targetTypes: Seq[String]) = new D06_EdgeTargetNodes(edgeType, targetTypes)

  def toNodes(nodeType: String) = new TargetsOfEdgesToNodes(edgeType, nodeType)

}
