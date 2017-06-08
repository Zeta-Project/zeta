package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class AttributesOfType(attributeType: String) {

  def inEdges(edgeType: String): AttributesOfTypeInEdges = new AttributesOfTypeInEdges(attributeType, edgeType)

  def inNodes(nodeType: String): AttributesOfTypeInNodes = new AttributesOfTypeInNodes(attributeType, nodeType)

  def inNodes(nodeTypes: Seq[String]): AttributesOfTypeInMultipleNodes = new AttributesOfTypeInMultipleNodes(attributeType, nodeTypes)

}
