package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

class AttributesOfType(attributeType: String) {

  def inEdges(edgeType: String): AttributesOfTypeInEdges = new AttributesOfTypeInEdges(attributeType, edgeType)

  def inNodes(nodeType: String): AttributesOfTypeInNodes = new AttributesOfTypeInNodes(attributeType, nodeType)

  def inNodes(nodeTypes: Seq[String]): AttributesOfTypeInMultipleNodes = new AttributesOfTypeInMultipleNodes(attributeType, nodeTypes)

}
