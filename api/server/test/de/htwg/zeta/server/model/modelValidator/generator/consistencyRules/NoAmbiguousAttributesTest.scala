package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NoAmbiguousAttributesTest extends FlatSpec with Matchers {

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
    description = "",
    abstractness = false,
    superTypeNames = Seq(),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(superClassAttribute),
    methods = Map.empty
  )

  val nonAmbiguousSuperClass = MClass(
    name = "nonAmbiguousSuperClass",
    description = "",
    abstractness = false,
    superTypeNames = Seq(),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(nonAmbiguousSuperClassAttribute),
    methods = Map.empty
  )

  val ambiguousSuperClass = MClass(
    name = "ambiguousSuperClass",
    description = "",
    abstractness = false,
    superTypeNames = Seq(),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(ambiguousSuperClassAttribute),
    methods = Map.empty
  )

  val nonAmbiguousSubClass = MClass(
    name = "nonAmbiguousSubClass",
    description = "",
    abstractness = false,
    superTypeNames = Seq(superClass.name, nonAmbiguousSuperClass.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Map.empty
  )

  val ambiguousSubClass = MClass(
    name = "ambiguousSubClass",
    description = "",
    abstractness = false,
    superTypeNames = Seq(superClass.name, ambiguousSuperClass.name),
    inputs = Seq(),
    outputs = Seq(),
    attributes = Seq(),
    methods = Map.empty
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

  val rule = new NoAmbiguousAttributes

  "check" should "return true on correct attribute inheritances" in {
    rule.check(nonAmbiguousMetaModel) should be (true)
  }

  it should "return false on ambiguous attribute inheritance" in {
    rule.check(ambiguousMetaModel) should be (false)
  }

}
