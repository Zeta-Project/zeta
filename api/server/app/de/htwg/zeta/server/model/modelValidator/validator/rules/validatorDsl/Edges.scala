package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.Edges

object Edges {

  def areOfTypes(edgeTypes: Seq[String]): Edges = new Edges(edgeTypes)

  def ofType(edgeType: String): EdgesOfType = new EdgesOfType(edgeType)

}
