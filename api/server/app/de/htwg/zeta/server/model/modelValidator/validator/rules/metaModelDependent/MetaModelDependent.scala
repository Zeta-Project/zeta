package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

object MetaModelDependent {

  val rules = Seq(
    D01_Edges,
    D01_Nodes,
    D02_EdgeAttributes,
    D02_NodeAttributes,
    D03_EdgeAttributesUpperBound,
    D03_NodeAttributesUpperBound,
    D04_EdgeAttributesLowerBound,
    D04_NodeAttributesLowerBound,
    D05_EdgeSourceNodes,
    D06_EdgeTargetNodes,
    D07_EdgeTargetsUpperBound,
    D08_EdgeTargetsLowerBound,
    D09_EdgeSourcesUpperBound,
    D10_EdgeSourcesLowerBound,
    D11_NodeInputEdges,
    D12_NodeOutputEdges,
    D13_NodeOutputsUpperBound,
    D14_NodeOutputsLowerBound,
    D15_NodeInputsUpperBound,
    D16_NodeInputsLowerBound,
    D17_EdgeAttributesLocalUnique,
    D17_NodeAttributesLocalUnique,
    D18_EdgeAttributesGlobalUnique,
    D18_NodeAttributesGlobalUnique,
    D19_EdgesNoAttributes,
    D19_NodesNoAttributes,
    D20_EdgesNoSources,
    D21_EdgesNoTargets,
    D22_NodesNoInputs,
    D23_NodesNoOutputs,
    D24_EdgeAttributeScalarTypes,
    D24_NodeAttributeScalarTypes,
    D25_EdgeAttributeEnumTypes,
    D25_NodeAttributeEnumTypes
  )

}
