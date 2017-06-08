package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule

object MetaModelDependent {

  val rules: Seq[GeneratorRule] = Seq(
    Edges,
    Nodes,
    EdgeAttributes,
    NodeAttributes,
    EdgeAttributesUpperBound,
    NodeAttributesUpperBound,
    EdgeAttributesLowerBound,
    NodeAttributesLowerBound,
    EdgeSourceNodes,
    EdgeTargetNodes,
    EdgeTargetsUpperBound,
    EdgeTargetsLowerBound,
    EdgeSourcesUpperBound,
    EdgeSourcesLowerBound,
    NodeInputEdges,
    NodeOutputEdges,
    NodeOutputsUpperBound,
    NodeOutputsLowerBound,
    NodeInputsUpperBound,
    NodeInputsLowerBound,
    EdgeAttributesLocalUnique,
    NodeAttributesLocalUnique,
    EdgeAttributesGlobalUnique,
    NodeAttributesGlobalUnique,
    EdgesNoAttributes,
    NodesNoAttributes,
    EdgesNoSources,
    EdgesNoTargets,
    NodesNoInputs,
    NodesNoOutputs,
    EdgeAttributeScalarTypes,
    NodeAttributeScalarTypes,
    EdgeAttributeEnumTypes,
    NodeAttributeEnumTypes
  )

}
