package de.htwg.zeta.server.model.modelValidator.validator.rules

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * A rule that can be used in the model validator generator.
 */
trait GeneratorRule {

  /**
   * Extracts the respective validation rules for every applicable part of the given meta model.
   *
   * @param metaModel The meta model.
   * @return A sequence of rules extracted from the meta model to validate the model against.
   */
  def generateFor(metaModel: MetaModel): Seq[DslRule]
}
