package de.htwg.zeta.server.model.modelValidator.validator

import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.EdgesRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.NodesRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent.EdgesAttributesNamesNotEmpty
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent.NodesAttributesNamesNotEmpty
import de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks.NullChecks
import de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks.NullChecks.NullChecksResult

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * Base class of all the generated validator classes for specific meta models.
 * Classes extending this trait will have to override the metaModelDependentRules as sequence of ElementsRule objects.
 */
trait ModelValidator {

  /**
   * The rules to validate a specific meta model with.
   *
   * Attention! This field will be referenced via reflection in
   * [[de.htwg.zeta.server.model.modelValidator.generator.ValidatorGenerator]]
   * If you intend to change the name or type, you also have to change it there!
   */
  val metaModelDependentNodesRules: Seq[NodesRule]
  val metaModelDependentEdgesRules: Seq[EdgesRule]

  /**
   * Validate a model against its the rules.
   * If the model contains null values, an invalid response will be returned.
   * Otherwise, it will be checked against the constant meta model independent rules,
   * and the overridden meta model dependent rules.
   *
   * @param model The model to validate.
   * @return The model validation result.
   */
  def validate(model: GraphicalDslInstance): Seq[ModelValidationResult] = {
    NullChecks.check(model) match {
      case NullChecksResult(false, Some(rule)) => Seq(ModelValidationResult(rule, valid = false))
      case _ =>
        new EdgesAttributesNamesNotEmpty().check(model.edges) ++
          new NodesAttributesNamesNotEmpty().check(model.nodes) ++
          // new ElementsIdsUnique().check(model) ++
          metaModelDependentNodesRules.flatMap(_.check(model.nodes)) ++
          metaModelDependentEdgesRules.flatMap(_.check(model.edges))


    }
  }

  /**
   * String representation of the meta model dependent rules sequence.
   *
   * @return The string representation.
   */
  override def toString: String = (metaModelDependentNodesRules ++ metaModelDependentEdgesRules).collect { case r: DslRule => r.dslStatement }.mkString("\n")
}
