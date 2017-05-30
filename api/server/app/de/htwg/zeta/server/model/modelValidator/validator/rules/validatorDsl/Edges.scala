package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.D01_Edges

object Edges {

  def areOfTypes(edgeTypes: Seq[String]) = new D01_Edges(edgeTypes)

  def ofType(edgeType: String) = new EdgesOfType(edgeType)

}
