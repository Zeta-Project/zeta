package de.htwg.zeta.server.model.modelValidator

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.modelDefinitions.model.elements.EdgeLink
import de.htwg.zeta.common.models.modelDefinitions.model.elements.NodeLink
import de.htwg.zeta.server.model.modelValidator.Util.Att
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class UtilTest extends FlatSpec with Matchers {


  val mClass = MClass(
    name = "mClass",
    description = "",
    abstractness = false,
    superTypeNames = Seq.empty,
    inputs = Seq.empty,
    outputs = Seq.empty,
    attributes = Seq[MAttribute](),
    methods = Seq.empty
  )

  val mReference = MReference(
    name = "mReference",
    description = "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    source = Seq.empty,
    target = Seq.empty,
    attributes = Seq[MAttribute](),
    methods = Seq.empty
  )

  val modelElements = Seq(
    Node(id = UUID.randomUUID(), className = mClass.name, outputs = Seq[EdgeLink](), inputs = Seq[EdgeLink](), attributeValues = Map.empty),
    Node(id = UUID.randomUUID(), className = mClass.name, outputs = Seq[EdgeLink](), inputs = Seq[EdgeLink](), attributeValues = Map.empty),
    Edge(id = UUID.randomUUID(), referenceName = mReference.name, source = Seq[NodeLink](), target = Seq[NodeLink](), attributeValues = Map.empty),
    Node(id = UUID.randomUUID(), className = mClass.name, outputs = Seq[EdgeLink](), inputs = Seq[EdgeLink](), attributeValues = Map.empty),
    Edge(id = UUID.randomUUID(), referenceName = mReference.name, source = Seq[NodeLink](), target = Seq[NodeLink](), attributeValues = Map.empty)
  )

  val mObjects = Seq(
    MReference(
      name = "mReference1",
      description = "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      source = Seq.empty,
      target = Seq.empty,
      attributes = Seq[MAttribute](),
      methods = Seq.empty
    ),
    MReference(
      name = "mReference2",
      description = "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      source = Seq.empty,
      target = Seq.empty,
      attributes = Seq[MAttribute](),
      methods = Seq.empty
    ),
    MClass(
      name = "mClass1",
      description = "",
      abstractness = false,
      superTypeNames = Seq.empty,
      inputs = Seq.empty,
      outputs = Seq.empty,
      attributes = Seq[MAttribute](),
      methods = Seq.empty
    ),
    MReference(
      name = "mReference3",
      description = "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      source = Seq.empty,
      target = Seq.empty,
      attributes = Seq[MAttribute](),
      methods = Seq.empty
    ),
    MClass(
      name = "mClass2",
      description = "",
      abstractness = true,
      superTypeNames = Seq.empty,
      inputs = Seq.empty,
      outputs = Seq.empty,
      attributes = Seq[MAttribute](),
      methods = Seq.empty
    )

  )


  val abstractSuperClassOneAttribute = MAttribute(
    name = "abstractSuperClassOneAttribute",
    globalUnique = false,
    localUnique = false,
    typ = StringType,
    default = StringValue(""),
    constant = false,
    singleAssignment = false,
    expression = "",
    ordered = false,
    transient = false,
    upperBound = -1,
    lowerBound = 0
  )

  val superClassOneToSuperClassTwo = MReference(
    name = "superClassOneToSuperClassTwo",
    description = "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    source = Seq(),
    target = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )
  val superClassOneOutput = MReferenceLinkDef(referenceName = superClassOneToSuperClassTwo.name, upperBound = -1, lowerBound = 0, deleteIfLower = false)
  val superClassTwoInput = MReferenceLinkDef(referenceName = superClassOneToSuperClassTwo.name, upperBound = -1, lowerBound = 0, deleteIfLower = false)

  val abstractSuperClassOne = MClass(
    name = "abstractSuperClassOne",
    description = "",
    abstractness = true,
    superTypeNames = Seq(),
    inputs = Seq(),
    outputs = Seq(superClassOneOutput),
    attributes = Seq(abstractSuperClassOneAttribute),
    methods = Seq.empty
  )
  val abstractSuperClassTwo = MClass(
    name = "abstractSuperClassTwo",
    description = "",
    abstractness = true,
    superTypeNames = Seq(),
    inputs = Seq(superClassTwoInput),
    outputs = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )
  val subClassOne = MClass(
    name = "subClassOne",
    description = "",
    abstractness = false,
    superTypeNames = Seq(abstractSuperClassOne.name, abstractSuperClassTwo.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )
  val subClassTwo = MClass(
    name = "subClassTwo",
    description = "",
    abstractness = false,
    superTypeNames = Seq(abstractSuperClassOne.name, abstractSuperClassTwo.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )

  val metaModel = MetaModel(
    name = "metaModelTest",
    classes = Seq(abstractSuperClassOne, abstractSuperClassTwo, subClassOne, subClassTwo),
    references = Seq(superClassOneToSuperClassTwo),
    enums = Seq.empty,
    attributes = Seq.empty,
    methods = Seq.empty,
    uiState = ""
  )

  val simplifiedGraph: scala.Seq[Util.El] = Util.simplifyMetaModelGraph(metaModel)


  "getNodes" should "return all nodes" in {
    val nodes = Util.getNodes(modelElements)
    nodes.size should be(3)
    nodes.map(_.id) should be(Seq("node1", "node2", "node3"))
    nodes.forall(_.isInstanceOf[Node]) should be(true)
  }

  "getEdges" should "return all edges" in {
    val edges = Util.getEdges(modelElements)
    edges.size should be(2)
    edges.map(_.id) should be(Seq("edge1", "edge2"))
    edges.forall(_.isInstanceOf[Edge]) should be(true)
  }

  "stringSeqToSeqString" should "return the correct string to use in DSL calls" in {
    val seq = Seq("a", "b", "c", "d")
    val seqString = Util.stringSeqToSeqString(seq)
    seqString should be(
      """Seq("a", "b", "c", "d")""")
  }

  "getAttributeTypeClassName" should "return the correct class name of an AttributeType" in {
    StringType.asString should be("String")
    DoubleType.asString should be("Double")
    BoolType.asString should be("Bool")
    IntType.asString should be("Int")

    val mEnum: MEnum = MEnum(name = "TestMEnum", valueNames = Seq.empty)

    mEnum.asString should be("TestMEnum")
  }

  "simplifyMetaModelGraph" should "convert the meta model to a simpler structure" in {
    val simplifiedGraph = Util.simplifyMetaModelGraph(metaModel)

    simplifiedGraph.size should be(4)

    simplifiedGraph.find(_.name == "subClassOne").get.superTypes.size should be(2)
    simplifiedGraph.find(_.name == "subClassOne").get.superTypes should be(Seq("abstractSuperClassOne", "abstractSuperClassTwo"))
    simplifiedGraph.find(_.name == "subClassOne").get.subTypes.size should be(0)

    simplifiedGraph.find(_.name == "subClassTwo").get.superTypes.size should be(2)
    simplifiedGraph.find(_.name == "subClassTwo").get.superTypes should be(Seq("abstractSuperClassOne", "abstractSuperClassTwo"))
    simplifiedGraph.find(_.name == "subClassTwo").get.subTypes.size should be(0)

    simplifiedGraph.find(_.name == "abstractSuperClassOne").get.superTypes.size should be(0)
    simplifiedGraph.find(_.name == "abstractSuperClassOne").get.subTypes.size should be(2)
    simplifiedGraph.find(_.name == "abstractSuperClassOne").get.subTypes should be(Seq("subClassOne", "subClassTwo"))

    simplifiedGraph.find(_.name == "abstractSuperClassTwo").get.superTypes.size should be(0)
    simplifiedGraph.find(_.name == "abstractSuperClassTwo").get.subTypes.size should be(2)
    simplifiedGraph.find(_.name == "abstractSuperClassTwo").get.subTypes should be(Seq("subClassOne", "subClassTwo"))
  }

  "inheritAttributes" should "inherit all attributes from superclasses to their child classes" in {

    val attributesInherited = Util.inheritAttributes(simplifiedGraph)

    attributesInherited.find(_.name == "abstractSuperClassOne").get.attributes.size should be(1)
    attributesInherited.find(_.name == "abstractSuperClassOne").get.attributes.head.name should be("abstractSuperClassOneAttribute")

    attributesInherited.find(_.name == "subClassOne").get.attributes.size should be(1)
    attributesInherited.find(_.name == "subClassOne").get.attributes.head.name should be("abstractSuperClassOneAttribute")

    attributesInherited.find(_.name == "subClassTwo").get.attributes.size should be(1)
    attributesInherited.find(_.name == "subClassTwo").get.attributes.head.name should be("abstractSuperClassOneAttribute")

  }

  it should "inherit attributes that are defined exactly the same" in {
    val elToRemove = simplifiedGraph.find(_.name == "abstractSuperClassTwo").get
    val abstractSuperClassTwoAttribute = simplifiedGraph.find(_.name == "abstractSuperClassOne").get.attributes
    val elToAdd = elToRemove.copy(attributes = abstractSuperClassTwoAttribute)

    val validAttributesInherited = simplifiedGraph.filterNot(_ == elToRemove) :+ elToAdd

    noException should be thrownBy Util.inheritAttributes(validAttributesInherited)

  }

  it should "fail on ambiguous attributes" in {

    val elToRemove = simplifiedGraph.find(_.name == "abstractSuperClassTwo").get
    val abstractSuperClassTwoAttribute = Att(
      name = "abstractSuperClassOneAttribute", // same name
      globalUnique = false,
      localUnique = false,
      `type` = BoolType, // different data type
      default = "false",
      constant = false,
      singleAssignment = false,
      expression = "",
      ordered = false,
      transient = false,
      upperBound = -1,
      lowerBound = 0
    )
    val elToAdd = elToRemove.copy(attributes = Seq(abstractSuperClassTwoAttribute))

    val invalidAttributeInherited = simplifiedGraph.filterNot(_ == elToRemove) :+ elToAdd

    an[IllegalStateException] should be thrownBy Util.inheritAttributes(invalidAttributeInherited)
  }

  "inheritInputs" should "inherit all inputs from superclasses to their child elements" in {

    val inheritedInputs = Util.inheritInputs(simplifiedGraph)

    inheritedInputs.find(_.name == "abstractSuperClassOne").get.inputs.size should be(0)

    inheritedInputs.find(_.name == "abstractSuperClassTwo").get.inputs.size should be(1)
    inheritedInputs.find(_.name == "abstractSuperClassTwo").get.inputs.head.name should be("superClassOneToSuperClassTwo")

    inheritedInputs.find(_.name == "subClassOne").get.inputs.size should be(1)
    inheritedInputs.find(_.name == "subClassOne").get.inputs.head.name should be("superClassOneToSuperClassTwo")

    inheritedInputs.find(_.name == "subClassTwo").get.inputs.size should be(1)
    inheritedInputs.find(_.name == "subClassTwo").get.inputs.head.name should be("superClassOneToSuperClassTwo")

  }

  it should "inherit all inputs that are defined exactly the same" in {
    val elToRemove = simplifiedGraph.find(_.name == "abstractSuperClassOne").get
    val abstractSuperClassOneInput = simplifiedGraph.find(_.name == "abstractSuperClassTwo").get.inputs.head.copy()
    val elToAdd = simplifiedGraph.find(_.name == "abstractSuperClassOne").get.copy(inputs = Seq(abstractSuperClassOneInput))

    val validInputsInherited = simplifiedGraph.filterNot(_ == elToRemove) :+ elToAdd

    noException should be thrownBy Util.inheritInputs(validInputsInherited)
  }

  it should "fail on ambiguous inputs" in {
    val elToRemove = simplifiedGraph.find(_.name == "abstractSuperClassOne").get
    val abstractSuperClassOneInput = simplifiedGraph.find(_.name == "abstractSuperClassTwo").get.inputs.head.copy(lowerBound = 5) // defined differently
    val elToAdd = simplifiedGraph.find(_.name == "abstractSuperClassOne").get.copy(inputs = Seq(abstractSuperClassOneInput))

    val invalidInputsInherited = simplifiedGraph.filterNot(_ == elToRemove) :+ elToAdd

    an[IllegalStateException] should be thrownBy Util.inheritInputs(invalidInputsInherited)
  }

  "inheritOutputs" should "inherit all outputs from superclasses to their child elements" in {
    val inheritedOutputs = Util.inheritOutputs(simplifiedGraph)

    inheritedOutputs.find(_.name == "abstractSuperClassTwo").get.outputs.size should be(0)

    inheritedOutputs.find(_.name == "abstractSuperClassOne").get.outputs.size should be(1)
    inheritedOutputs.find(_.name == "abstractSuperClassOne").get.outputs.head.name should be("superClassOneToSuperClassTwo")

    inheritedOutputs.find(_.name == "subClassOne").get.outputs.size should be(1)
    inheritedOutputs.find(_.name == "subClassOne").get.outputs.head.name should be("superClassOneToSuperClassTwo")

    inheritedOutputs.find(_.name == "subClassTwo").get.outputs.size should be(1)
    inheritedOutputs.find(_.name == "subClassTwo").get.outputs.head.name should be("superClassOneToSuperClassTwo")
  }

  it should "inherit all outputs that are defined exactly the same" in {
    val elToRemove = simplifiedGraph.find(_.name == "abstractSuperClassTwo").get
    val abstractSuperClassOneOutput = simplifiedGraph.find(_.name == "abstractSuperClassOne").get.outputs.head.copy()
    val elToAdd = simplifiedGraph.find(_.name == "abstractSuperClassTwo").get.copy(outputs = Seq(abstractSuperClassOneOutput))

    val validOutputsInherited = simplifiedGraph.filterNot(_ == elToRemove) :+ elToAdd

    noException should be thrownBy Util.inheritOutputs(validOutputsInherited)

  }

  it should "fail on ambiguous outputs" in {
    val elToRemove = simplifiedGraph.find(_.name == "abstractSuperClassTwo").get
    val abstractSuperClassOneOutput = simplifiedGraph.find(_.name == "abstractSuperClassOne").get.outputs.head.copy(lowerBound = 5) // defined differently
    val elToAdd = simplifiedGraph.find(_.name == "abstractSuperClassTwo").get.copy(outputs = Seq(abstractSuperClassOneOutput))

    val invalidOutputsInherited = simplifiedGraph.filterNot(_ == elToRemove) :+ elToAdd

    an[IllegalStateException] should be thrownBy Util.inheritOutputs(invalidOutputsInherited)
  }

}
