package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeAttributes

class AttributesInEdges(edgeType: String) {

  def areOfTypes(attributeTypes: Seq[String]) = new EdgeAttributes(edgeType, attributeTypes)

}
