package de.htwg.zeta.server.model.modelValidator.validator.rules.nullChecks

import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule
import models.modelDefinitions.model.Model

object NullChecks {

  case class NullChecksResult(valid: Boolean = true, rule: Option[ModelRule] = None)

  def check(model: Model): NullChecksResult = {
    rules.foldLeft(NullChecksResult()) { (acc, rule) =>
      if (acc.valid) {
        if (rule.check(model)) acc
        else NullChecksResult(valid = false, rule = Some(rule))
      } else acc
    }
  }

  private val rules = Seq(
    new B01_ElementsNotNull,
    new B02_ElementsNoNullValues,
    new B03_ElementsIdNotNull,
    new B04_NodeTypeNotNull,
    new B05_NodeOutputsNotNull,
    new B06_NodeOutputsNoNullValues,
    new B07_NodeInputsNotNull,
    new B08_NodeInputsNoNullValues,
    new B09_NodeAttributesNotNull,
    new B10_NodeAttributesNoNullValues,
    new B11_NodeAttributesNamesNotNull,
    new B12_NodeAttributesValuesNotNull,
    new B13_NodeAttributesValuesNoNullValues,
    new B14_NodeOutputsTypeNotNull,
    new B15_NodeOutputsEdgesNotNull,
    new B16_NodeOutputsEdgesNoNullValues,
    new B17_NodeInputsTypeNotNull,
    new B18_NodeInputsEdgesNotNull,
    new B19_NodeInputsEdgesNoNullValues,
    new B20_EdgeTypeNotNull,
    new B21_EdgeSourcesNotNull,
    new B22_EdgeSourcesNoNullValues,
    new B23_EdgeTargetsNotNull,
    new B24_EdgeTargetsNoNullValues,
    new B25_EdgeAttributesNotNull,
    new B26_EdgeAttributesNoNullValues,
    new B27_EdgeAttributesNamesNotNull,
    new B28_EdgeAttributesValuesNotNull,
    new B29_EdgeAttributesValuesNoNullValues,
    new B30_EdgeSourcesTypeNotNull,
    new B31_EdgeSourcesNodesNotNull,
    new B32_EdgeSourcesNodesNoNullValues,
    new B33_EdgeTargetsTypeNotNull,
    new B34_EdgeTargetsNodesNotNull,
    new B35_EdgeTargetsNodesNoNullValues
  )

}
