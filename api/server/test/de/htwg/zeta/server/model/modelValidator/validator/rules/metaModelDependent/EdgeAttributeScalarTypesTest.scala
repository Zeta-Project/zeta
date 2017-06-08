package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MEnum
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.metaModel.elements.ScalarType
import models.modelDefinitions.metaModel.elements.ScalarValue.MInt
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeAttributeScalarTypesTest extends FlatSpec with Matchers {

  val mReference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new EdgeAttributeScalarTypes("reference", "attributeType", ScalarType.String)

  "the rule" should "be true for valid edges" in {
    val attribute = Attribute(name = "attributeType", value = Seq(MString("value")))
    val edge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(attribute))

    rule.isValid(edge).get should be (true)
  }

  it should "be false for invalid edges" in {
    val attribute = Attribute(name = "attributeType", value = Seq(MInt(42)))
    val edge = Edge.apply2("edgeId", mReference, Seq(), Seq(), Seq(attribute))

    rule.isValid(edge).get should be (false)
  }

  it should "return None for non-matching edges" in {
    val differentMReference = MReference("differentMReference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val attribute = Attribute(name = "attributeType", value = Seq(MString("value")))
    val edge = Edge.apply2("edgeId", differentMReference, Seq(), Seq(), Seq(attribute))

    rule.isValid(edge) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Attributes ofType "attributeType" inEdges "reference" areOfScalarType "String"""")
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
    val result = EdgeAttributeScalarTypes.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgeAttributeScalarTypes =>
        rule.edgeType should be ("reference")
        rule.attributeType should be ("attributeName2")
        rule.attributeDataType should be (ScalarType.String)
      case _ => fail
    }
  }

}
