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
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DslTest extends AnyFlatSpec with Matchers {

  private val emptyString = ""

  "Attributes" should "return the correct attribute instances" in {

    Attributes inEdges emptyString areOfTypes Seq() shouldBe a[EdgeAttributes]
    Attributes inNodes emptyString areOfTypes Seq() shouldBe a[NodeAttributes]

    Attributes ofType emptyString inEdges emptyString areGlobalUnique() shouldBe a[EdgesAttributesGlobalUnique]
    Attributes ofType emptyString inEdges emptyString areOfScalarType "String" shouldBe a[EdgeAttributeScalarTypes]
    Attributes ofType emptyString inEdges emptyString areOfEnumType emptyString shouldBe a[EdgeAttributeEnumTypes]

    Attributes ofType emptyString inNodes emptyString areOfScalarType "Bool" shouldBe a[NodeAttributeScalarTypes]
    Attributes ofType emptyString inNodes emptyString areOfEnumType emptyString shouldBe a[NodeAttributeEnumTypes]

    Attributes ofType emptyString inNodes Seq() areGlobalUnique() shouldBe a[NodeAttributesGlobalUnique]
  }

  "Edges" should "return the correct edge instances" in {

    Edges areOfTypes Seq() shouldBe a[Edges]

    Edges ofType emptyString haveNoAttributes() shouldBe a[EdgesNoAttributes]
    Edges ofType emptyString haveNoSources() shouldBe a[EdgesNoSources]
    Edges ofType emptyString haveNoTargets() shouldBe a[EdgesNoTargets]
  }

  "Nodes" should "return the correct node instances" in {

    Nodes areOfTypes Seq() shouldBe a[Nodes]

    Nodes ofType emptyString haveNoAttributes() shouldBe a[NodesNoAttributes]
    Nodes ofType emptyString haveNoInputs() shouldBe a[NodesNoInputs]
    Nodes ofType emptyString haveNoOutputs() shouldBe a[NodesNoOutputs]
  }

  "Sources" should "return the correct source instances" in {

    Sources ofEdges emptyString areOfTypes Seq() shouldBe a[EdgeSourceNodes]

  }

  "Targets" should "return the correct target instances" in {

    Targets ofEdges emptyString areOfTypes Seq() shouldBe a[EdgeTargetNodes]

  }

  "Inputs" should "return the correct input instances" in {

    Inputs ofNodes emptyString areOfTypes Seq() shouldBe a[NodeInputEdges]

  }

  "Outputs" should "return the correct output instances" in {

    Outputs ofNodes emptyString areOfTypes Seq() shouldBe a[NodeOutputEdges]

  }

}
