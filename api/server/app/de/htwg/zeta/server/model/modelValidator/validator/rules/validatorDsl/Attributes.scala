package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

object Attributes {

  def inEdges(edgeType: String): AttributesInEdges = new AttributesInEdges(edgeType)

  def inNodes(nodeType: String): AttributesInNodes = new AttributesInNodes(nodeType)

  def ofType(attributeType: String): AttributesOfType = new AttributesOfType(attributeType)
}
