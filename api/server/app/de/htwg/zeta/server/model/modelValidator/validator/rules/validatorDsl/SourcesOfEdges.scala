package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeSourceNodes

class SourcesOfEdges(edgeType: String) {

  def areOfTypes(sourceTypes: Seq[String]) = new EdgeSourceNodes(edgeType, sourceTypes)

  def toNodes(nodeType: String) = new SourcesOfEdgesToNodes(edgeType, nodeType)

}
