package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import scala.util.Try

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.server.model.modelValidator.Util

class NoAmbiguousOutputs extends ConceptRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "Different super types must not define outputs having the same name differently."

  override def check(concept: Concept): Boolean = {
    Try(Util.inheritOutputs(concept.classes)).isSuccess
  }
}
