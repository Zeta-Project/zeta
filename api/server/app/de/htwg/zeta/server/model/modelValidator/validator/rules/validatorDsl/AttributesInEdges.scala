package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeAttributes

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class AttributesInEdges(edgeType: String) {

  def areOfTypes(attributeTypes: Seq[String]): EdgeAttributes = new EdgeAttributes(edgeType, attributeTypes)

}
