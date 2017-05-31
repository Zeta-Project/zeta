package de.htwg.zeta.server.model.modelValidator

import scala.collection.immutable.Seq

import de.htwg.zeta.server.model.modelValidator.Util.Att
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MEnum
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MObject
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.metaModel.elements.ScalarType
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.Node
import models.modelDefinitions.model.elements.ToEdges
import models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class UtilTest extends FlatSpec with Matchers {


    val mClass = MClass(
      name = "mClass",
      abstractness = false,
      superTypes = Seq[MClass](),
      inputs = Seq[MLinkDef](),
      outputs = Seq[MLinkDef](),
      attributes = Seq[MAttribute]()
    )

    val mReference = MReference(
      name = "mReference",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      source = Seq[MLinkDef](),
      target = Seq[MLinkDef](),
      attributes = Seq[MAttribute]()
    )

    val modelElements = Seq(
      Node.apply2(id = "node1", `type` = mClass, outputs = Seq[ToEdges](), inputs = Seq[ToEdges](), attributes = Seq[Attribute]()),
      Node.apply2(id = "node2", `type` = mClass, outputs = Seq[ToEdges](), inputs = Seq[ToEdges](), attributes = Seq[Attribute]()),
      Edge.apply2(id = "edge1", `type` = mReference, source = Seq[ToNodes](), target = Seq[ToNodes](), attributes = Seq[Attribute]()),
      Node.apply2(id = "node3", `type` = mClass, outputs = Seq[ToEdges](), inputs = Seq[ToEdges](), attributes = Seq[Attribute]()),
      Edge.apply2(id = "edge2", `type` = mReference, source = Seq[ToNodes](), target = Seq[ToNodes](), attributes = Seq[Attribute]())
    )

    val mObjects = Seq(
      MReference(name = "mReference1", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, source = Seq[MLinkDef](), target = Seq[MLinkDef](), attributes = Seq[MAttribute]()),
      MReference(name = "mReference2", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, source = Seq[MLinkDef](), target = Seq[MLinkDef](), attributes = Seq[MAttribute]()),
      MClass(name = "mClass1", abstractness = false, superTypes = Seq[MClass](), inputs = Seq[MLinkDef](), outputs = Seq[MLinkDef](), attributes = Seq[MAttribute]()),
      MReference(name = "mReference3", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, source = Seq[MLinkDef](), target = Seq[MLinkDef](), attributes = Seq[MAttribute]()),
      MClass(name = "mClass2", abstractness = true, superTypes = Seq[MClass](), inputs = Seq[MLinkDef](), outputs = Seq[MLinkDef](), attributes = Seq[MAttribute]())

    )

    val inheritanceMObjects: Seq[MObject] = {

      val abstractSuperClassOneAttribute = MAttribute(
        name = "abstractSuperClassOneAttribute",
        globalUnique = false,
        localUnique = false,
        `type` = ScalarType.String,
        default = MString(""),
        constant = false,
        singleAssignment = false,
        expression = "",
        ordered = false,
        transient = false,
        upperBound = -1,
        lowerBound = 0
      )

      val superClassOneToSuperClassTwo = MReference(name = "superClassOneToSuperClassTwo", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, source = Seq(), target = Seq(), attributes = Seq())
      val superClassOneOutput = MLinkDef(mType = superClassOneToSuperClassTwo, upperBound = -1, lowerBound = 0, deleteIfLower = false)
      val superClassTwoInput = MLinkDef(mType = superClassOneToSuperClassTwo, upperBound = -1, lowerBound = 0, deleteIfLower = false)

      val abstractSuperClassOne = MClass(name = "abstractSuperClassOne", abstractness = true, superTypes = Seq(), inputs = Seq(), outputs = Seq(superClassOneOutput), attributes = Seq(abstractSuperClassOneAttribute))
      val abstractSuperClassTwo = MClass(name = "abstractSuperClassTwo", abstractness = true, superTypes = Seq(), inputs = Seq(superClassTwoInput), outputs = Seq(), attributes = Seq())
      val subClassOne = MClass(name = "subClassOne", abstractness = false, superTypes = Seq(abstractSuperClassOne, abstractSuperClassTwo), inputs = Seq(), outputs = Seq(), attributes = Seq())
      val subClassTwo = MClass(name = "subClassTwo", abstractness = false, superTypes = Seq(abstractSuperClassOne, abstractSuperClassTwo), inputs = Seq(), outputs = Seq(), attributes = Seq())

      Seq(abstractSuperClassOne, abstractSuperClassTwo, subClassOne, subClassTwo, superClassOneToSuperClassTwo)
    }

    val metaModel = MetaModel(
      name = "metaModelTest",
      elements = inheritanceMObjects.map(obj => obj.name -> obj).toMap,
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
    seqString should be("""Seq("a", "b", "c", "d")""")
  }

  "getReferences" should "return all mReferences" in {
    val references = Util.getReferences(mObjects)
    references.size should be(3)
    references.map(_.name) should be(Seq("mReference1", "mReference2", "mReference3"))
    references.forall(_.isInstanceOf[MReference]) should be(true)
  }

  "getClasses" should "return all mClasses" in {
    val classes = Util.getClasses(mObjects)
    classes.size should be(2)
    classes.map(_.name) should be(Seq("mClass1", "mClass2"))
    classes.forall(_.isInstanceOf[MClass]) should be(true)
  }

  "getAttributeTypeClassName" should "return the correct class name of an AttributeType" in {
    Util.getAttributeTypeClassName(ScalarType.String) should be("String")
    Util.getAttributeTypeClassName(ScalarType.Double) should be("Double")
    Util.getAttributeTypeClassName(ScalarType.Bool) should be("Bool")
    Util.getAttributeTypeClassName(ScalarType.Int) should be("Int")

    val mEnum: MEnum = MEnum(
      name = "TestMEnum",
      values = Seq()
    )

    Util.getAttributeTypeClassName(mEnum) should be("MEnum")
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
      `type` = ScalarType.Bool, // different data type
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

    noException should be thrownBy(Util.inheritOutputs(validOutputsInherited))

  }

  it should "fail on ambiguous outputs" in {
    val elToRemove = simplifiedGraph.find(_.name == "abstractSuperClassTwo").get
    val abstractSuperClassOneOutput = simplifiedGraph.find(_.name == "abstractSuperClassOne").get.outputs.head.copy(lowerBound = 5) // defined differently
    val elToAdd = simplifiedGraph.find(_.name == "abstractSuperClassTwo").get.copy(outputs = Seq(abstractSuperClassOneOutput))

    val invalidOutputsInherited = simplifiedGraph.filterNot(_ == elToRemove) :+ elToAdd

    an[IllegalStateException] should be thrownBy Util.inheritOutputs(invalidOutputsInherited)
  }

}
