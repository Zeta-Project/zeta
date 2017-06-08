package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MEnum
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.metaModel.elements.ScalarType
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributeEnumTypesTest extends FlatSpec with Matchers {

  val mReference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new EdgeAttributeEnumTypes("reference", "attributeType", "enumName")

  "the rule" should "be true for valid edges" in {
    val mEnum = MEnum(name = "enumName", values = Seq())
    val attribute = Attribute(name = "attributeType", value = Seq(new EnumSymbol("enumName", mEnum)))
    val edge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(attribute))

    rule.isValid(edge).get should be(true)
  }

  it should "return None for non-matching edge" in {
    val differentMReference = MReference("differentMReference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val edge = Edge.apply2("edgeId", differentMReference, Seq(), Seq(), Seq())

    rule.isValid(edge) should be (None)
  }

  it should "be false for invalid edges" in {
    val differentEnum = MEnum(name = "differentEnumName", values = Seq())
    val attribute = Attribute(name = "attributeType", value = Seq(new EnumSymbol("differentEnumName", differentEnum)))
    val edge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(attribute))

    rule.isValid(edge).get should be(false)
  }

  it should "be None for non-matching edges" in {
    val differentReference = MReference("differentRef", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val edge = Edge.apply2("", differentReference, Seq(), Seq(), Seq())
    rule.isValid(edge) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Attributes ofType "attributeType" inEdges "reference" areOfEnumType "enumName"""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val tmpEnumType = MEnum("enumName", Seq())

    val enumValues = Seq(
      EnumSymbol("enumValue1", tmpEnumType),
      EnumSymbol("enumValue2", tmpEnumType)
    )

    val enumType = tmpEnumType.copy(values = enumValues)
    val enumAttribute = MAttribute("attributeName", globalUnique = false, localUnique = false, enumType, enumValues.head, constant = false, singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val scalarAttribute = MAttribute("attributeName2", globalUnique = false, localUnique = false, ScalarType.String, MString(""), constant = false, singleAssignment = false, "", ordered = false, transient = false, -1, 0)
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute](enumAttribute, scalarAttribute))
    val metaModel = TestUtil.toMetaModel(Seq(reference))
    val result = EdgeAttributeEnumTypes.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgeAttributeEnumTypes =>
        rule.edgeType should be ("reference")
        rule.attributeType should be ("attributeName")
        rule.enumName should be ("enumName")
      case _ => fail
    }

  }

}
