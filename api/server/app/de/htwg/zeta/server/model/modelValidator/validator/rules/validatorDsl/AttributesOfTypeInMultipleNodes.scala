package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributesGlobalUnique

class AttributesOfTypeInMultipleNodes(attributeType: String, nodeTypes: Seq[String]) {

  def areGlobalUnique(): NodeAttributesGlobalUnique = new NodeAttributesGlobalUnique(nodeTypes, attributeType)

}
