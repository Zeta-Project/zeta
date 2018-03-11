package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import de.htwg.zeta.common.models.modelDefinitions.concept.Concept

trait ConceptRule {

  val name: String
  val description: String

  def check(concept: Concept): Boolean

  override def toString: String = s"$name: $description"

}
