package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule
import de.htwg.zeta.common.models.modelDefinitions.model.Model

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 *
 * Checks the whole model for null values.
 * NULL should be avoided and should not be used scala programming. However, the user could via the REST API provide a
 * meta model containing null values. These will be parsed and saved correctly as NULL. To prevent
 * NullPointerExceptions, a model containing NULL will be regarded as invalid and can not be validated further.
 */
object NullChecks {

  /**
   * Result of one null check rule
   *
   * @param valid Was the check valid?
   * @param rule  The rule that was checked.
   */
  case class NullChecksResult(valid: Boolean, rule: Option[ModelRule])

  /**
   * Checks a model against all of the null check rules.
   *
   * @param model The model.
   * @return The result.
   */
  def check(model: Model): NullChecksResult = {
    rules.foldLeft(NullChecksResult(valid = true, rule = None)) { (acc, rule) =>
      if (acc.valid) {
        if (rule.check(model)) {
          acc
        } else {
          NullChecksResult(valid = false, rule = Some(rule))
        }
      } else {
        acc
      }
    }
  }

  /**
   * All null check rules which the model will be chacked against.
   */
  val rules: Seq[ModelRule] = Seq(
    new ElementsNotNull,
    new ElementsNoNullValues,
    new ElementsIdNotNull,
    new NodeTypeNotNull,
    new NodeOutputsNotNull,
    new NodeOutputsNoNullValues,
    new NodeInputsNotNull,
    new NodeInputsNoNullValues,
    new NodeAttributesNotNull,
    new NodeAttributesNoNullValues,
    new NodeAttributesNamesNotNull,
    new NodeAttributesValuesNotNull,
    new NodeAttributesValuesNoNullValues,
    new NodeOutputsTypeNotNull,
    new NodeOutputsEdgesNotNull,
    new NodeOutputsEdgesNoNullValues,
    new NodeInputsTypeNotNull,
    new NodeInputsEdgesNotNull,
    new NodeInputsEdgesNoNullValues,
    new EdgeTypeNotNull,
    new EdgeSourcesNotNull,
    new EdgeSourcesNoNullValues,
    new EdgeTargetsNotNull,
    new EdgeTargetsNoNullValues,
    new EdgeAttributesNotNull,
    new EdgeAttributesNoNullValues,
    new EdgeAttributesNamesNotNull,
    new EdgeAttributesValuesNotNull,
    new EdgeAttributesValuesNoNullValues,
    new EdgeSourcesTypeNotNull,
    new EdgeSourcesNodesNotNull,
    new EdgeSourcesNodesNoNullValues,
    new EdgeTargetsTypeNotNull,
    new EdgeTargetsNodesNotNull,
    new EdgeTargetsNodesNoNullValues
  )

}
