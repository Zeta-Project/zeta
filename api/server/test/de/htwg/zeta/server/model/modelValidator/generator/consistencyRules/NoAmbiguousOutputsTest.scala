package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NoAmbiguousOutputsTest extends FlatSpec with Matchers {
  val output = MReferenceLinkDef(
    referenceName = "output",
    upperBound = -1,
    lowerBound = 0,
    deleteIfLower = false
  )

  val superClass = MClass(
    name = "superClass",
    description = "",
    abstractness = false,
    superTypeNames = Seq(),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(output),
    attributes = Seq(),
    methods = Seq.empty
  )

  val nonAmbiguousOutput = MReferenceLinkDef(
    referenceName = "output",
    upperBound = -1,
    lowerBound = 0,
    deleteIfLower = false
  )

  val ambiguousOutput = MReferenceLinkDef(
    referenceName = "output",
    upperBound = 5, // different upperBound
    lowerBound = 0,
    deleteIfLower = false
  )

  val nonAmbiguousSuperClass = MClass(
    name = "nonAmbiguousSuperClass",
    description = "",
    abstractness = false,
    superTypeNames = Seq(),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(nonAmbiguousOutput),
    attributes = Seq(),
    methods = Seq.empty
  )

  val ambiguousSuperClass = MClass(
    name = "ambiguousSubClass",
    description = "",
    abstractness = false,
    superTypeNames = Seq(),
    inputReferenceNames = Seq(),
    outputReferenceNames = Seq(ambiguousOutput),
    attributes = Seq(),
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
    name = "ambiguousMetaMOdel",
    classes = Seq(superClass, ambiguousSuperClass, ambiguousSubClass),
    references = Seq(),
    enums = Seq.empty,
    methods = Seq.empty,
    attributes = Seq.empty,
    uiState = ""
  )

  val rule = new NoAmbiguousOutputs

  "check" should "return true on a meta model with correct input inheritance" in {
    rule.check(nonAmbiguousMetaModel) should be (true)
  }

  it should "return false on a meta model with incorrect input inheritance" in {
    rule.check(ambiguousMetaModel) should be (false)
  }
}
