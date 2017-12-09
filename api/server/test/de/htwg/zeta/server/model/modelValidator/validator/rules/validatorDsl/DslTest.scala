package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeAttributeEnumTypes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeAttributeScalarTypes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeAttributes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeSourceNodes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeTargetNodes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.Edges
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgesAttributesGlobalUnique
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgesNoAttributes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgesNoSources
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgesNoTargets
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributeEnumTypes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributeScalarTypes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributesGlobalUnique
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeInputEdges
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeOutputEdges
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.Nodes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodesNoAttributes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodesNoInputs
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodesNoOutputs
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class DslTest extends FlatSpec with Matchers {

  "Attributes" should "return the correct instances" in {

    Attributes inEdges "" areOfTypes Seq() shouldBe a[EdgeAttributes]
    Attributes inNodes "" areOfTypes Seq() shouldBe a[NodeAttributes]

    Attributes ofType "" inEdges "" areGlobalUnique() shouldBe a[EdgesAttributesGlobalUnique]
    Attributes ofType "" inEdges "" areOfScalarType "String" shouldBe a[EdgeAttributeScalarTypes]
    Attributes ofType "" inEdges "" areOfEnumType "" shouldBe a[EdgeAttributeEnumTypes]

    Attributes ofType "" inNodes "" areOfScalarType "Bool" shouldBe a[NodeAttributeScalarTypes]
    Attributes ofType "" inNodes "" areOfEnumType "" shouldBe a[NodeAttributeEnumTypes]

    Attributes ofType "" inNodes Seq() areGlobalUnique() shouldBe a[NodeAttributesGlobalUnique]
  }

  "Edges" should "return the correct instances" in {

    Edges areOfTypes Seq() shouldBe a[Edges]

    Edges ofType "" haveNoAttributes() shouldBe a[EdgesNoAttributes]
    Edges ofType "" haveNoSources() shouldBe a[EdgesNoSources]
    Edges ofType "" haveNoTargets() shouldBe a[EdgesNoTargets]
  }

  "Nodes" should "return the correct instances" in {

    Nodes areOfTypes Seq() shouldBe a[Nodes]

    Nodes ofType "" haveNoAttributes() shouldBe a[NodesNoAttributes]
    Nodes ofType "" haveNoInputs() shouldBe a[NodesNoInputs]
    Nodes ofType "" haveNoOutputs() shouldBe a[NodesNoOutputs]
  }

  "Sources" should "return the correct instances" in {

    Sources ofEdges "" areOfTypes Seq() shouldBe a[EdgeSourceNodes]

  }

  "Targets" should "return the correct instances" in {

    Targets ofEdges "" areOfTypes Seq() shouldBe a[EdgeTargetNodes]

  }

  "Inputs" should "return the correct instances" in {

    Inputs ofNodes "" areOfTypes Seq() shouldBe a[NodeInputEdges]

  }

  "Outputs" should "return the correct instances" in {

    Outputs ofNodes "" areOfTypes Seq() shouldBe a[NodeOutputEdges]

  }

}
