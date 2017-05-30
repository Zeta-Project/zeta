package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.Nodes

object Nodes {

  def areOfTypes(nodeTypes: Seq[String]) = new Nodes(nodeTypes)

  def ofType(nodeType: String) = new NodesOfType(nodeType)

}
