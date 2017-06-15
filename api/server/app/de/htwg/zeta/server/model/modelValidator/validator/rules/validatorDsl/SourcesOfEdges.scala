package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeSourceNodes

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class SourcesOfEdges(edgeType: String) {

  def areOfTypes(sourceTypes: Seq[String]): EdgeSourceNodes = new EdgeSourceNodes(edgeType, sourceTypes)

  def toNodes(nodeType: String): SourcesOfEdgesToNodes = new SourcesOfEdgesToNodes(edgeType, nodeType)

}
