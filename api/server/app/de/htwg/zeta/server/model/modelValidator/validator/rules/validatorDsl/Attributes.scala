package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

object Attributes {

  def inEdges(edgeType: String) = new AttributesInEdges(edgeType)

  def inNodes(nodeType: String) = new AttributesInNodes(nodeType)

  def ofType(attributeType: String) = new AttributesOfType(attributeType)
}
