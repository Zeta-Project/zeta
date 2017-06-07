package de.htwg.zeta.server.model.modelValidator.validator

import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.ElementsRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent.MetaModelIndependent
import de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks.NullChecks
import de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks.NullChecks.NullChecksResult
import models.modelDefinitions.model.Model

trait ModelValidator {

  val metaModelDependentRules: Seq[ElementsRule]

  def validate(model: Model): ModelValidationResultContainer = {
    val results = NullChecks.check(model) match {
      case NullChecksResult(false, Some(rule)) => Seq(ModelValidationResult(rule, valid = false))
      case _ => (MetaModelIndependent.rules ++ metaModelDependentRules).flatMap(_.check(model.elements.values.toSeq))
    }
    ModelValidationResultContainer(results)
  }

  override def toString: String = metaModelDependentRules.collect { case r: DslRule => r.dslStatement }.mkString("\n")
}
