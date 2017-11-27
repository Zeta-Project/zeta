package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule

/**
 * Collection of all the rules the model can be validated against which are dependent on the meta model.
 */
object MetaModelDependent {

  /**
   * All possible meta model dependent rules.
   */
  val rules: Seq[GeneratorRule] = Seq(
    Edges,
    Nodes,
    EdgeAttributes,
    NodeAttributes,
    EdgeSourceNodes,
    EdgeTargetNodes,
    NodeInputEdges,
    NodeOutputEdges,
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
