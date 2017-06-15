package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeSourcesLowerBoundTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Set.empty,
    Set.empty,
    Set[MAttribute]()
  )
  val rule = new EdgeSourcesLowerBound("edgeType", "sourceType", 2)

  "isValid" should "return true on edges of type edgeType having 2 or more source nodes of type sourceType" in {
    val sourceType = MClass(
      name = "sourceType",
      abstractness = false,
      superTypeNames = Set(),
      inputs = Set(),
      outputs = Set(),
      attributes = Set()
    )

    val twoSourceNodes = ToNodes(clazz = sourceType, nodeNames = Set("1", "2"))

    val edgeTwoSourceNodes = Edge("", mReference, Set(twoSourceNodes), Set(), Map.empty)

    rule.isValid(edgeTwoSourceNodes).get should be(true)

    val threeSourceNodes = ToNodes(clazz = sourceType, nodeNames = Set("1", "2", "2"))

    val edgeThreeSourceNodes = Edge("", mReference, Set(threeSourceNodes), Set(), Map.empty)

    rule.isValid(edgeThreeSourceNodes).get should be(true)
  }

  it should "return false on edges of type edgeType having less than 2 source nodes of type sourceType" in {
    val sourceType = MClass(
      name = "sourceType",
      abstractness = false,
      superTypeNames = Set(),
      inputs = Set(),
      outputs = Set(),
      attributes = Set()
    )

    val oneSourceNode = ToNodes(clazz = sourceType, nodeNames = Set("1"))

    val edgeOneSourceNode = Edge("", mReference, Set(oneSourceNode), Set(), Map.empty)

    rule.isValid(edgeOneSourceNode).get should be(false)

    val edgeNoSourceNodes = Edge("", mReference, Set(), Set(), Map.empty)

    rule.isValid(edgeNoSourceNodes).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentMRef = MReference(
      "invalidReference",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Set.empty,
      Set.empty,
      Set[MAttribute]()
    )
    val edge = Edge("", differentMRef, Set(), Set(), Map.empty)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Sources ofEdges "edgeType" toNodes "sourceType" haveLowerBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val class1 = MClass("class", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val sourceLinkDef1 = MClassLinkDef(class1.name, -1, 5, deleteIfLower = false)
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(sourceLinkDef1), Set.empty,
      Set[MAttribute]())
    val metaModel = TestUtil.referencesToMetaModel(Set(reference))
    val result = EdgeSourcesLowerBound.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeSourcesLowerBound =>
        rule.edgeType should be("reference")
        rule.sourceType should be("class")
        rule.lowerBound should be(5)
      case _ => fail
    }
  }

}
