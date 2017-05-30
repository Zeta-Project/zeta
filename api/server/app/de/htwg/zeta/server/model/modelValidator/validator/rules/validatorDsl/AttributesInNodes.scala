package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.D02_NodeAttributes

class AttributesInNodes(nodeType: String) {

  def areOfTypes(attributeType: Seq[String]) = new D02_NodeAttributes(nodeType, attributeType)

}