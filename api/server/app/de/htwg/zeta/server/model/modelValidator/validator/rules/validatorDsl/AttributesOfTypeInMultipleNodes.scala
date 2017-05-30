package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.D18_NodeAttributesGlobalUnique

class AttributesOfTypeInMultipleNodes(attributeType: String, nodeTypes: Seq[String]) {

  def areGlobalUnique() = new D18_NodeAttributesGlobalUnique(nodeTypes, attributeType)

}
