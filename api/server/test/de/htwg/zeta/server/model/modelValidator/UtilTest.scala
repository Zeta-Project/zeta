package de.htwg.zeta.server.model.modelValidator

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UtilTest extends AnyFlatSpec with Matchers {


  private val emptyString = ""

  val mClass = MClass(
    name = "mClass",
    description = emptyString,
    abstractness = false,
    superTypeNames = Seq.empty,
    inputReferenceNames = Seq.empty,
    outputReferenceNames = Seq.empty,
    attributes = Seq[MAttribute](),
    methods = Seq.empty
  )

  val mReference = MReference(
    name = "mReference",
    description = emptyString,
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    sourceClassName = emptyString,
    targetClassName = emptyString,
    sourceLowerBounds = 0,
    sourceUpperBounds = 0,
    targetLowerBounds = 0,
    targetUpperBounds = 0,
    attributes = Seq.empty,
    methods = Seq.empty
  )

  val modelNodes = Seq(
    NodeInstance.empty("node1", mClass.name, Seq.empty, Seq.empty),
    NodeInstance.empty("node2", mClass.name, Seq.empty, Seq.empty),
    NodeInstance.empty("node3", mClass.name, Seq.empty, Seq.empty)
  )

  val modelEdges = Seq(
    EdgeInstance.empty("edge1", mReference.name, emptyString, emptyString),
    EdgeInstance.empty("edge2", mReference.name, emptyString, emptyString)
  )

  val mObjects = Seq(
    MReference(
      name = "mReference1",
      description = emptyString,
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      sourceClassName = emptyString,
      targetClassName = emptyString,
      sourceLowerBounds = 0,
      sourceUpperBounds = 0,
      targetLowerBounds = 0,
      targetUpperBounds = 0,
      attributes = Seq.empty,
      methods = Seq.empty
    ),
    MReference(
      name = "mReference2",
      description = emptyString,
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      sourceClassName = emptyString,
      targetClassName = emptyString,
      sourceLowerBounds = 0,
      sourceUpperBounds = 0,
      targetLowerBounds = 0,
      targetUpperBounds = 0,
      attributes = Seq.empty,
      methods = Seq.empty
    ),
    MClass(
      name = "mClass1",
      description = emptyString,
      abstractness = false,
      superTypeNames = Seq.empty,
      inputReferenceNames = Seq.empty,
      outputReferenceNames = Seq.empty,
      attributes = Seq[MAttribute](),
      methods = Seq.empty
    ),
    MReference(
      name = "mReference3",
      description = emptyString,
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      sourceClassName = emptyString,
      targetClassName = emptyString,
      sourceLowerBounds = 0,
      sourceUpperBounds = 0,
      targetLowerBounds = 0,
      targetUpperBounds = 0,
      attributes = Seq.empty,
      methods = Seq.empty
    ),
    MClass(
      name = "mClass2",
      description = emptyString,
      abstractness = true,
      superTypeNames = Seq.empty,
      inputReferenceNames = Seq.empty,
      outputReferenceNames = Seq.empty,
      attributes = Seq[MAttribute](),
      methods = Seq.empty
    )

  )


  val abstractSuperClassOneAttribute = MAttribute(
    name = "abstractSuperClassOneAttribute",
    globalUnique = false,
    localUnique = false,
    typ = StringType,
    default = StringValue(emptyString),
    constant = false,
    singleAssignment = false,
    expression = emptyString,
    ordered = false,
    transient = false
  )

  val superClassOneToSuperClassTwo = MReference(
    name = "superClassOneToSuperClassTwo",
    description = emptyString,
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    sourceClassName = emptyString,
    targetClassName = emptyString,
    sourceLowerBounds = 0,
    sourceUpperBounds = 0,
    targetLowerBounds = 0,
    targetUpperBounds = 0,
    attributes = Seq.empty,
    methods = Seq.empty
  )

  val abstractSuperClassOne = MClass(
    name = "abstractSuperClassOne",
    description = emptyString,
    abstractness = true,
    superTypeNames = Seq.empty,
    inputReferenceNames = Seq.empty,
    outputReferenceNames = Seq(superClassOneToSuperClassTwo.name),
    attributes = Seq(abstractSuperClassOneAttribute),
    methods = Seq.empty
  )
  val abstractSuperClassTwo = MClass(
    name = "abstractSuperClassTwo",
    description = emptyString,
    abstractness = true,
    superTypeNames = Seq.empty,
    inputReferenceNames = Seq(superClassOneToSuperClassTwo.name),
    outputReferenceNames = Seq.empty,
    attributes = Seq.empty,
    methods = Seq.empty
  )
  val subClassOne = MClass(
    name = "subClassOne",
    description = emptyString,
    abstractness = false,
    superTypeNames = Seq(abstractSuperClassOne.name, abstractSuperClassTwo.name),
    inputReferenceNames = Seq.empty,
    outputReferenceNames = Seq.empty,
    attributes = Seq.empty,
    methods = Seq.empty
  )
  val subClassTwo = MClass(
    name = "subClassTwo",
    description = emptyString,
    abstractness = false,
    superTypeNames = Seq(abstractSuperClassOne.name, abstractSuperClassTwo.name),
    inputReferenceNames = Seq.empty,
    outputReferenceNames = Seq.empty,
    attributes = Seq.empty,
    methods = Seq.empty
  )

  val concept = Concept(
    classes = Seq(abstractSuperClassOne, abstractSuperClassTwo, subClassOne, subClassTwo),
    references = Seq(superClassOneToSuperClassTwo),
    enums = Seq.empty,
    attributes = Seq.empty,
    methods = Seq.empty,
    uiState = emptyString
  )


  "stringSeqToSeqString" should "return the correct string to use in DSL calls" ignore {
    val seq = Seq("a", "b", "c", "d")
    val seqString = Util.stringSeqToSeqString(seq)
    seqString should be(
      """Seq("a", "b", "c", "d")""")
  }

  "inheritAttributes" should "inherit all attributes from superclasses to their child classes" ignore {

    val attributesInherited = Util.inheritAttributes(concept.classes)

    attributesInherited.find(_.name == "abstractSuperClassOne").get.attributes.size should be(1)
    attributesInherited.find(_.name == "abstractSuperClassOne").get.attributes.head.name should be("abstractSuperClassOneAttribute")

    attributesInherited.find(_.name == "subClassOne").get.attributes.size should be(1)
    attributesInherited.find(_.name == "subClassOne").get.attributes.head.name should be("abstractSuperClassOneAttribute")

    attributesInherited.find(_.name == "subClassTwo").get.attributes.size should be(1)
    attributesInherited.find(_.name == "subClassTwo").get.attributes.head.name should be("abstractSuperClassOneAttribute")

  }

  it should "inherit attributes that are defined exactly the same" in {
    val elToRemove = concept.classes.find(_.name == "abstractSuperClassTwo").get
    val abstractSuperClassTwoAttribute = concept.classes.find(_.name == "abstractSuperClassOne").get.attributes
    val elToAdd = elToRemove.copy(attributes = abstractSuperClassTwoAttribute)

    val validAttributesInherited = concept.classes.filterNot(_ == elToRemove) :+ elToAdd

    noException should be thrownBy Util.inheritAttributes(validAttributesInherited)

  }

  it should "fail on ambiguous attributes" ignore {

    val elToRemove = concept.classes.find(_.name == "abstractSuperClassTwo").get
    val abstractSuperClassTwoAttribute = MAttribute(
      name = "abstractSuperClassOneAttribute", // same name
      globalUnique = false,
      localUnique = false,
      typ = BoolType, // different data type
      default = BoolValue(false),
      constant = false,
      singleAssignment = false,
      expression = emptyString,
      ordered = false,
      transient = false
    )
    val elToAdd = elToRemove.copy(attributes = Seq(abstractSuperClassTwoAttribute))

    val invalidAttributeInherited = concept.classes.filterNot(_ == elToRemove) :+ elToAdd

    an[IllegalStateException] should be thrownBy Util.inheritAttributes(invalidAttributeInherited)
  }

  "inheritInputs" should "inherit all inputs from superclasses to their child elements" ignore {

    val inheritedInputs = Util.inheritInputs(concept.classes)

    inheritedInputs.find(_.name == "abstractSuperClassOne").get.inputReferenceNames.size should be(0)

    inheritedInputs.find(_.name == "abstractSuperClassTwo").get.inputReferenceNames.size should be(1)
    inheritedInputs.find(_.name == "abstractSuperClassTwo").get.inputReferenceNames.head should be("superClassOneToSuperClassTwo")

    inheritedInputs.find(_.name == "subClassOne").get.inputReferenceNames.size should be(1)
    inheritedInputs.find(_.name == "subClassOne").get.inputReferenceNames.head should be("superClassOneToSuperClassTwo")

    inheritedInputs.find(_.name == "subClassTwo").get.inputReferenceNames.size should be(1)
    inheritedInputs.find(_.name == "subClassTwo").get.inputReferenceNames.head should be("superClassOneToSuperClassTwo")

  }

  it should "inherit all inputs that are defined exactly the same" in {
    val elToRemove = concept.classes.find(_.name == "abstractSuperClassOne").get
    val abstractSuperClassOneInput = concept.classes.find(_.name == "abstractSuperClassTwo").get.inputReferenceNames.head
    val elToAdd = concept.classes.find(_.name == "abstractSuperClassOne").get.copy(inputReferenceNames = Seq(abstractSuperClassOneInput))

    val validInputsInherited = concept.classes.filterNot(_ == elToRemove) :+ elToAdd

    noException should be thrownBy Util.inheritInputs(validInputsInherited)
  }

  "inheritOutputs" should "inherit all outputs from superclasses to their child elements" ignore {
    val inheritedOutputs = Util.inheritOutputs(concept.classes)

    inheritedOutputs.find(_.name == "abstractSuperClassTwo").get.outputReferenceNames.size should be(0)

    inheritedOutputs.find(_.name == "abstractSuperClassOne").get.outputReferenceNames.size should be(1)
    inheritedOutputs.find(_.name == "abstractSuperClassOne").get.outputReferenceNames.head should be("superClassOneToSuperClassTwo")

    inheritedOutputs.find(_.name == "subClassOne").get.outputReferenceNames.size should be(1)
    inheritedOutputs.find(_.name == "subClassOne").get.outputReferenceNames.head should be("superClassOneToSuperClassTwo")

    inheritedOutputs.find(_.name == "subClassTwo").get.outputReferenceNames.size should be(1)
    inheritedOutputs.find(_.name == "subClassTwo").get.outputReferenceNames.head should be("superClassOneToSuperClassTwo")
  }

  it should "inherit all outputs that are defined exactly the same" in {
    val elToRemove = concept.classes.find(_.name == "abstractSuperClassTwo").get
    val abstractSuperClassOneOutput = concept.classes.find(_.name == "abstractSuperClassOne").get.outputReferenceNames.head
    val elToAdd = concept.classes.find(_.name == "abstractSuperClassTwo").get.copy(outputReferenceNames = Seq(abstractSuperClassOneOutput))

    val validOutputsInherited = concept.classes.filterNot(_ == elToRemove) :+ elToAdd

    noException should be thrownBy Util.inheritOutputs(validOutputsInherited)

  }

}
