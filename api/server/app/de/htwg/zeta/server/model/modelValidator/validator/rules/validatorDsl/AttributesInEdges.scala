package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.D02_EdgeAttributes

class AttributesInEdges(edgeType: String) {

  def areOfTypes(attributeTypes: Seq[String]) = new D02_EdgeAttributes(edgeType, attributeTypes)

}