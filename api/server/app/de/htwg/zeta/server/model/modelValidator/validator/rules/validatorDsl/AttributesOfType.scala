package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

class AttributesOfType(attributeType: String) {

  def inEdges(edgeType: String) = new AttributesOfTypeInEdges(attributeType, edgeType)

  def inNodes(nodeType: String) = new AttributesOfTypeInNodes(attributeType, nodeType)

  def inNodes(nodeTypes: Seq[String]) = new AttributesOfTypeInMultipleNodes(attributeType, nodeTypes)

}
