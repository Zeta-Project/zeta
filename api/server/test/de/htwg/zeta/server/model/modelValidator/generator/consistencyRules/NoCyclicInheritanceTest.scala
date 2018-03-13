package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.Concept
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NoCyclicInheritanceTest extends FlatSpec with Matchers {

  val nonCyclicClassOne = MClass(
    name = "nonCyclicClassOne",
    description = "",
    abstractness = false,
    superTypeNames = Seq(),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )

  val nonCyclicClassTwo = MClass(
    name = "nonCyclicClassTwo",
    description = "",
    abstractness = false,
    superTypeNames = Seq(nonCyclicClassOne.name),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )

  val nonCyclicClassThree = MClass(
    name = "nonCyclicClassThree",
    description = "",
    abstractness = false,
    superTypeNames = Seq(nonCyclicClassTwo.name),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )

  val nonCyclicClassFour = MClass(
    name = "nonCyclicClassFour",
    description = "",
    abstractness = false,
    superTypeNames = Seq(nonCyclicClassTwo.name),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )

  val nonCyclicMetaModel = Concept(
    classes = Seq(nonCyclicClassOne, nonCyclicClassTwo, nonCyclicClassThree, nonCyclicClassFour),
    references = Seq(),
    enums = Seq.empty,
    methods = Seq.empty,
    attributes = Seq.empty,
    uiState = ""
  )

  val cyclicClassOne = MClass(
    name = "cyclicClassOne",
    description = "",
    abstractness = false,
    superTypeNames = Seq("cyclicClassFour"),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )

  val cyclicClassTwo = MClass(
    name = "cyclicClassTwo",
    description = "",
    abstractness = false,
    superTypeNames = Seq(cyclicClassOne.name),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )

  val cyclicClassThree = MClass(
    name = "cyclicClassThree",
    description = "",
    abstractness = false,
    superTypeNames = Seq(cyclicClassTwo.name),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )

  val cyclicClassFour = MClass(
    name = "cyclicClassFour",
    description = "",
    abstractness = false,
    superTypeNames = Seq(cyclicClassThree.name),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )

  val cyclicClassFive = MClass(
    name = "cyclicClassFive",
    description = "",
    abstractness = false,
    superTypeNames = Seq(cyclicClassThree.name),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )

  val cyclicClassSix = MClass(
    name = "cyclicClassSix",
    description = "",
    abstractness = false,
    superTypeNames = Seq(),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(),
    attributes = Seq(),
    methods = Seq.empty
  )

  val cyclicMetaModel = Concept(
    classes = Seq(cyclicClassOne, cyclicClassTwo, cyclicClassThree, cyclicClassFour, cyclicClassFive, cyclicClassSix),
    references = Seq(),
    enums = Seq.empty,
    methods = Seq.empty,
    attributes = Seq.empty,
    uiState = ""
  )

  val rule = new NoCyclicInheritance

  "check" should "return true on non-cyclic meta models" in {
    rule.check(nonCyclicMetaModel) should be (true)
  }

  it should "return false on cyclic meta models" in {
    rule.check(cyclicMetaModel) should be (false)
  }

}
