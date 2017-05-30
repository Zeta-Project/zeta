package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.D01_Nodes

object Nodes {

  def areOfTypes(nodeTypes: Seq[String]) = new D01_Nodes(nodeTypes)

  def ofType(nodeType: String) = new NodesOfType(nodeType)

}
