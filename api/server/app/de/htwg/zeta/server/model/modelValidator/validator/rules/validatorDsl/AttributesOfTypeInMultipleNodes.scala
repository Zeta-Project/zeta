package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributesGlobalUnique

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class AttributesOfTypeInMultipleNodes(attributeType: String, nodeTypes: Seq[String]) {

  def areGlobalUnique(): NodeAttributesGlobalUnique = new NodeAttributesGlobalUnique(nodeTypes, attributeType)

}
