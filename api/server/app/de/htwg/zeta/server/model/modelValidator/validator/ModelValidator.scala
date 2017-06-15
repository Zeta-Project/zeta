package de.htwg.zeta.server.model.modelValidator.validator

import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.ElementsRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent.MetaModelIndependent
import de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks.NullChecks
import de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks.NullChecks.NullChecksResult
import de.htwg.zeta.common.models.modelDefinitions.model.Model

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * Base class of all the generated validator classes for specific meta models.
 * Classes extending this trait will have to override the metaModelDependentRules as sequence of ElementsRule objects.
 */
trait ModelValidator {

  /**
   * The rules to validate a specific meta model with.
   */
  val metaModelDependentRules: Seq[ElementsRule]

  /**
   * Validate a model against its the rules.
   * If the model contains null values, an invalid response will be returned.
   * Otherwise, it will be checked against the constant meta model independent rules,
   * and the overridden meta model dependent rules.
   *
   * @param model The model to validate.
   * @return The model validation result.
   */
  def validate(model: Model): Seq[ModelValidationResult] = {
    NullChecks.check(model) match {
      case NullChecksResult(false, Some(rule)) => Seq(ModelValidationResult(rule, valid = false))
      case _ => (MetaModelIndependent.rules ++ metaModelDependentRules).flatMap(_.check(model.nodes.values.toSeq ++ model.edges.values.toSeq))
    }
  }

  /**
   * String representation of the meta model dependent rules sequence.
   *
   * @return The string representation.
   */
  override def toString: String = metaModelDependentRules.collect { case r: DslRule => r.dslStatement }.mkString("\n")
}
