package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent._
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class DslTest extends FlatSpec with Matchers{

  "Attributes" should "return the correct instances" in {

    Attributes inEdges "" areOfTypes Seq() shouldBe a [EdgeAttributes]
    Attributes inNodes "" areOfTypes Seq() shouldBe a [NodeAttributes]

    Attributes ofType "" inEdges "" haveUpperBound 0 shouldBe a [EdgeAttributesUpperBound]
    Attributes ofType "" inEdges "" haveLowerBound 0 shouldBe a [EdgeAttributesLowerBound]
    Attributes ofType "" inEdges "" areLocalUnique () shouldBe a [EdgeAttributesLocalUnique]
    Attributes ofType "" inEdges "" areGlobalUnique () shouldBe a [EdgesAttributesGlobalUnique]
    Attributes ofType "" inEdges "" areOfScalarType "String" shouldBe a [EdgeAttributeScalarTypes]
    Attributes ofType "" inEdges "" areOfEnumType "" shouldBe a [EdgeAttributeEnumTypes]

    Attributes ofType "" inNodes "" haveUpperBound 0 shouldBe a [NodeAttributesUpperBound]
    Attributes ofType "" inNodes "" haveLowerBound 0 shouldBe a [NodeAttributesLowerBound]
    Attributes ofType "" inNodes "" areLocalUnique () shouldBe a [NodeAttributesLocalUnique]
    Attributes ofType "" inNodes "" areOfScalarType "Bool" shouldBe a [NodeAttributeScalarTypes]
    Attributes ofType "" inNodes "" areOfEnumType "" shouldBe a [NodeAttributeEnumTypes]

    Attributes ofType "" inNodes Seq() areGlobalUnique () shouldBe a [NodeAttributesGlobalUnique]
  }

  "Edges" should "return the correct instances" in {

    Edges areOfTypes Seq() shouldBe a [Edges]

    Edges ofType "" haveNoAttributes () shouldBe a [EdgesNoAttributes]
    Edges ofType "" haveNoSources () shouldBe a [EdgesNoSources]
    Edges ofType "" haveNoTargets () shouldBe a [EdgesNoTargets]
  }

  "Nodes" should "return the correct instances" in {

    Nodes areOfTypes Seq() shouldBe a [Nodes]

    Nodes ofType "" haveNoAttributes () shouldBe a [NodesNoAttributes]
    Nodes ofType "" haveNoInputs () shouldBe a [NodesNoInputs]
    Nodes ofType "" haveNoOutputs() shouldBe a [NodesNoOutputs]
  }

  "Sources" should "return the correct instances" in {

    Sources ofEdges "" areOfTypes Seq() shouldBe a [EdgeSourceNodes]

    Sources ofEdges "" toNodes "" haveLowerBound 0 shouldBe a [EdgeSourcesLowerBound]
    Sources ofEdges "" toNodes "" haveUpperBound 0 shouldBe a [EdgeSourcesUpperBound]
  }

  "Targets" should "return the correct instances" in {

    Targets ofEdges "" areOfTypes Seq() shouldBe a [EdgeTargetNodes]

    Targets ofEdges "" toNodes "" haveLowerBound 0 shouldBe a [EdgeTargetsLowerBound]
    Targets ofEdges "" toNodes "" haveUpperBound 0 shouldBe a [EdgeTargetsUpperBound]
  }

  "Inputs" should "return the correct instances" in {

    Inputs ofNodes "" areOfTypes Seq() shouldBe a [NodeInputEdges]

    Inputs ofNodes "" toEdges "" haveLowerBound 0 shouldBe a [NodeInputsLowerBound]
    Inputs ofNodes "" toEdges "" haveUpperBound 0 shouldBe a [NodeInputsUpperBound]
  }

  "Outputs" should "return the correct instances" in {

    Outputs ofNodes "" areOfTypes Seq() shouldBe a [NodeOutputEdges]

    Outputs ofNodes "" toEdges "" haveLowerBound 0 shouldBe a [NodeOutputsLowerBound]
    Outputs ofNodes "" toEdges "" haveUpperBound 0 shouldBe a [NodeOutputsUpperBound]
  }

}
