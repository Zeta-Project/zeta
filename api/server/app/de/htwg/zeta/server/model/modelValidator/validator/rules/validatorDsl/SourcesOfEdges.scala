package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.D05_EdgeSourceNodes

class SourcesOfEdges(edgeType: String) {

  def areOfTypes(sourceTypes: Seq[String]) = new D05_EdgeSourceNodes(edgeType, sourceTypes)

  def toNodes(nodeType: String) = new SourcesOfEdgesToNodes(edgeType, nodeType)

}
