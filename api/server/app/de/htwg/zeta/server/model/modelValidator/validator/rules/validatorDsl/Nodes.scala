package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.Nodes

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
object Nodes {

  def areOfTypes(nodeTypes: Seq[String]): Nodes = new Nodes(nodeTypes)

  def ofType(nodeType: String): NodesOfType = new NodesOfType(nodeType)

}
