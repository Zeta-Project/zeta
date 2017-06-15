package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesNoAttributesTest extends FlatSpec with Matchers {
  val mReference = MReference(
    "edgeType",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Set.empty,
    Set.empty,
    Set[MAttribute]()
  )
  val rule = new EdgesNoAttributes("edgeType")

  "isValid" should "return true on edges of type edgeType with no attributes" in {
    val edge = Edge("", mReference, Set(), Set(), Map.empty)
    rule.isValid(edge).get should be(true)
  }

  it should "return false on edges of type edgeType with attributes" in {
    val attribute: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set(MString("att")))
    val edge = Edge("", mReference, Set(), Set(), attribute)
    rule.isValid(edge).get should be(false)
  }

  it should "return true on edges of type edgeType with empty attribute values" in {
    val attribute: Map[String, Set[AttributeValue]] = Map("attributeType" -> Set())
    val edge = Edge("", mReference, Set(), Set(), attribute)
    rule.isValid(edge).get should be(true)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference(
      "differentEdgeType",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Set.empty,
      Set.empty,
      Set[MAttribute]()
    )
    val edge = Edge("", differentReference, Set(), Set(), Map.empty)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Edges ofType "edgeType" haveNoAttributes ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set.empty, Set.empty, Set[MAttribute]())
    val metaModel = TestUtil.referencesToMetaModel(Set(reference))
    val result = EdgesNoAttributes.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgesNoAttributes =>
        rule.edgeType should be ("reference")
      case _ => fail
    }
  }
}
