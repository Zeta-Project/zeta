package de.htwg.zeta.server.model.modelValidator.generator

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.server.model.modelValidator.generator.consistencyRules.NoAmbiguousAttributes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

import scala.collection.immutable.Seq

class MetaModelConsistencyCheckerTest extends FlatSpec with Matchers {

  val superClassAttribute = MAttribute(
    name = "superClassAttribute",
    globalUnique = false,
    localUnique = false,
    typ = StringType,
    default = MString(""),
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
    default = MString(""),
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
    default = MString(""),
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
    abstractness = false,
    superTypeNames = Seq(),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(superClassAttribute)
  )

  val nonAmbiguousSuperClass = MClass(
    name = "nonAmbiguousSuperClass",
    abstractness = false,
    superTypeNames = Seq(),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(nonAmbiguousSuperClassAttribute)
  )

  val ambiguousSuperClass = MClass(
    name = "ambiguousSuperClass",
    abstractness = false,
    superTypeNames = Seq(),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(ambiguousSuperClassAttribute)
  )

  val nonAmbiguousSubClass = MClass(
    name = "nonAmbiguousSubClass",
    abstractness = false,
    superTypeNames = Seq(superClass.name, nonAmbiguousSuperClass.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val ambiguousSubClass = MClass(
    name = "ambiguousSubClass",
    abstractness = false,
    superTypeNames = Seq(superClass.name, ambiguousSuperClass.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val nonAmbiguousMetaModel = MetaModel(
    name = "nonAmbiguousMetaModel",
    classes = Seq(superClass, nonAmbiguousSuperClass, nonAmbiguousSubClass),
    references = Seq(),
    enums = Seq.empty,
    uiState = ""
  )

  val ambiguousMetaModel = MetaModel(
    name = "ambiguousMetaModel",
    classes = Seq(superClass, ambiguousSuperClass, ambiguousSubClass),
    references = Seq(),
    enums = Seq.empty,
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
