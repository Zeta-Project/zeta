package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import de.htwg.zeta.server.model.modelValidator.Util
import models.modelDefinitions.metaModel.MetaModel

import scala.util.Try

class NoAmbiguousAttributes extends MetaModelRule {

  override val name: String = getClass.getSimpleName
  override val description: String = "Different super types must not define attributes having the same name differently."

  override def check(metaModel: MetaModel): Boolean = {
    val simplifiedGraph = Util.simplifyMetaModelGraph(metaModel)
    Try(Util.inheritAttributes(simplifiedGraph)).isSuccess
  }

}

