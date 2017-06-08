package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * Starting keyword of the DSL.
 */
object Attributes {

  def inEdges(edgeType: String): AttributesInEdges = new AttributesInEdges(edgeType)

  def inNodes(nodeType: String): AttributesInNodes = new AttributesInNodes(nodeType)

  def ofType(attributeType: String): AttributesOfType = new AttributesOfType(attributeType)
}
