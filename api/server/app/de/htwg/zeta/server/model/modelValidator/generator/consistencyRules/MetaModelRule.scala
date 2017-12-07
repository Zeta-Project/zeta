package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept

trait MetaModelRule {

  val name: String
  val description: String

  def check(metaModel: Concept): Boolean

  override def toString: String = s"$name: $description"

}
