package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributes

class AttributesInNodes(nodeType: String) {

  def areOfTypes(attributeType: Seq[String]): NodeAttributes = new NodeAttributes(nodeType, attributeType)

}
