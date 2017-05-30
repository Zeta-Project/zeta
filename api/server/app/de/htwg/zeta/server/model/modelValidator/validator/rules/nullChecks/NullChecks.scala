package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule
import models.modelDefinitions.model.Model

object NullChecks {

  case class NullChecksResult(valid: Boolean = true, rule: Option[ModelRule] = None)

  def check(model: Model): NullChecksResult = {
    rules.foldLeft(NullChecksResult()) { (acc, rule) =>
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

  private val rules = Seq(
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
