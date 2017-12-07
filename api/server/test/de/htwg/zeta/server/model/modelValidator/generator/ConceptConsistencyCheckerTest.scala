package de.htwg.zeta.server.model.modelValidator.generator

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.server.model.modelValidator.generator.consistencyRules.NoAmbiguousAttributes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class ConceptConsistencyCheckerTest extends FlatSpec with Matchers {

  val superClassAttribute = MAttribute(
    name = "superClassAttribute",
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

  val nonAmbiguousSuperClassAttribute = MAttribute(
    name = "superClassAttribute",
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

  val ambiguousSuperClassAttribute = MAttribute(
    name = "superClassAttribute",
    globalUnique = true, // different value
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

  val superClass = MClass(
    name = "superClass",
    description = "",
    abstractness = false,
    superTypeNames = Seq(),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(superClassAttribute),
    methods = Seq.empty
  )

  val nonAmbiguousSuperClass = MClass(
    name = "nonAmbiguousSuperClass",
    description = "",
    abstractness = false,
    superTypeNames = Seq(),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(nonAmbiguousSuperClassAttribute),
    methods = Seq.empty
  )

  val ambiguousSuperClass = MClass(
    name = "ambiguousSuperClass",
    description = "",
    abstractness = false,
    superTypeNames = Seq(),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(ambiguousSuperClassAttribute),
    methods = Seq.empty
  )

  val nonAmbiguousSubClass = MClass(
    name = "nonAmbiguousSubClass",
    description = "",
    abstractness = false,
    superTypeNames = Seq(superClass.name, nonAmbiguousSuperClass.name),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )

  val ambiguousSubClass = MClass(
    name = "ambiguousSubClass",
    description = "",
    abstractness = false,
    superTypeNames = Seq(superClass.name, ambiguousSuperClass.name),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )

  val nonAmbiguousMetaModel = Concept(
    name = "nonAmbiguousMetaModel",
    classes = Seq(superClass, nonAmbiguousSuperClass, nonAmbiguousSubClass),
    references = Seq(),
    enums = Seq.empty,
    methods = Seq.empty,
    attributes = Seq.empty,
    uiState = ""
  )

  val ambiguousMetaModel = Concept(
    name = "ambiguousMetaModel",
    classes = Seq(superClass, ambiguousSuperClass, ambiguousSubClass),
    references = Seq(),
    enums = Seq.empty,
    methods = Seq.empty,
    attributes = Seq.empty,
    uiState = ""
  )

  "checkConsistencs" should "return a valid result on a valid meta model" in {
    val checker = new MetaModelConsistencyChecker(nonAmbiguousMetaModel)
    val result = checker.checkConsistency()
    result.valid should be (true)
  }

  it should "return a invalid result on invalid meta model" in {
    val checker = new MetaModelConsistencyChecker(ambiguousMetaModel)
    val result = checker.checkConsistency()
    result.valid should be (false)
    result.failedRule.get shouldBe a[NoAmbiguousAttributes]
  }

}