package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NoCyclicInheritanceTest extends FlatSpec with Matchers {

  val nonCyclicClassOne = MClass(
    name = "nonCyclicClassOne",
    description = "",
    abstractness = false,
    superTypeNames = Seq(),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Map.empty
  )

  val nonCyclicClassTwo = MClass(
    name = "nonCyclicClassTwo",
    description = "",
    abstractness = false,
    superTypeNames = Seq(nonCyclicClassOne.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Map.empty
  )

  val nonCyclicClassThree = MClass(
    name = "nonCyclicClassThree",
    description = "",
    abstractness = false,
    superTypeNames = Seq(nonCyclicClassTwo.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Map.empty
  )

  val nonCyclicClassFour = MClass(
    name = "nonCyclicClassFour",
    description = "",
    abstractness = false,
    superTypeNames = Seq(nonCyclicClassTwo.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Map.empty
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
    description = "",
    abstractness = false,
    superTypeNames = Seq("cyclicClassFour"),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Map.empty
  )

  val cyclicClassTwo = MClass(
    name = "cyclicClassTwo",
    description = "",
    abstractness = false,
    superTypeNames = Seq(cyclicClassOne.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Map.empty
  )

  val cyclicClassThree = MClass(
    name = "cyclicClassThree",
    description = "",
    abstractness = false,
    superTypeNames = Seq(cyclicClassTwo.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Map.empty
  )

  val cyclicClassFour = MClass(
    name = "cyclicClassFour",
    description = "",
    abstractness = false,
    superTypeNames = Seq(cyclicClassThree.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Map.empty
  )

  val cyclicClassFive = MClass(
    name = "cyclicClassFive",
    description = "",
    abstractness = false,
    superTypeNames = Seq(cyclicClassThree.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Map.empty
  )

  val cyclicClassSix = MClass(
    name = "cyclicClassSix",
    description = "",
    abstractness = false,
    superTypeNames = Seq(),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Map.empty
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
