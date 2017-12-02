package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import scala.util.Try

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept

class NoAmbiguousOutputs extends MetaModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "Different super types must not define outputs having the same name differently."

  override def check(metaModel: Concept): Boolean = {
    val simplifiedGraph = Util.simplifyMetaModelGraph(metaModel)
    Try(Util.inheritOutputs(simplifiedGraph)).isSuccess
  }
}
