package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import scala.util.Try

import de.htwg.zeta.common.models.modelDefinitions.concept.Concept
import de.htwg.zeta.server.model.modelValidator.Util

class NoAmbiguousInputs extends ConceptRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "Different super types must not define inputs having the same name differently."

  override def check(concept: Concept): Boolean = {
    Try(Util.inheritInputs(concept.classes)).isSuccess
  }
}
