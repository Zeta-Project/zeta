package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import org.scalatest.FlatSpec
import org.scalatest.Matchers

import scala.collection.immutable.Seq

class NoCyclicInheritanceTest extends FlatSpec with Matchers {

  val nonCyclicClassOne = MClass(
    name = "nonCyclicClassOne",
    abstractness = false,
    superTypeNames = Seq(),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val nonCyclicClassTwo = MClass(
    name = "nonCyclicClassTwo",
    abstractness = false,
    superTypeNames = Seq(nonCyclicClassOne.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val nonCyclicClassThree = MClass(
    name = "nonCyclicClassThree",
    abstractness = false,
    superTypeNames = Seq(nonCyclicClassTwo.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val nonCyclicClassFour = MClass(
    name = "nonCyclicClassFour",
    abstractness = false,
    superTypeNames = Seq(nonCyclicClassTwo.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val nonCyclicMetaModel = MetaModel(
    name = "metaModelTest",
    classes = Seq(nonCyclicClassOne, nonCyclicClassTwo, nonCyclicClassThree, nonCyclicClassFour),
    references = Seq(),
    enums = Seq.empty,
    uiState = ""
  )

  val cyclicClassOne = MClass(
    name = "cyclicClassOne",
    abstractness = false,
    superTypeNames = Seq("cyclicClassFour"),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val cyclicClassTwo = MClass(
    name = "cyclicClassTwo",
    abstractness = false,
    superTypeNames = Seq(cyclicClassOne.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val cyclicClassThree = MClass(
    name = "cyclicClassThree",
    abstractness = false,
    superTypeNames = Seq(cyclicClassTwo.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val cyclicClassFour = MClass(
    name = "cyclicClassFour",
    abstractness = false,
    superTypeNames = Seq(cyclicClassThree.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val cyclicClassFive = MClass(
    name = "cyclicClassFive",
    abstractness = false,
    superTypeNames = Seq(cyclicClassThree.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val cyclicClassSix = MClass(
    name = "cyclicClassSix",
    abstractness = false,
    superTypeNames = Seq(),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq()
  )

  val cyclicMetaModel = MetaModel(
    name = "metaModelTest",
    classes = Seq(cyclicClassOne, cyclicClassTwo, cyclicClassThree, cyclicClassFour, cyclicClassFive, cyclicClassSix),
    references = Seq(),
    enums = Seq.empty,
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
