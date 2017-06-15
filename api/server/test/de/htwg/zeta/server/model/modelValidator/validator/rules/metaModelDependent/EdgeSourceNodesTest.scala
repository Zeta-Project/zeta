package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeSourceNodesTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Set.empty,
    Set.empty,
    Set[MAttribute]()
  )
  val rule = new EdgeSourceNodes("edgeType", Seq("source1", "source2"))

  "isValid" should "return true on edges of type edgeType with valid source nodes" in {

    val source1 = MClass(
      name = "source1",
      abstractness = false,
      superTypeNames = Set(),
      inputs = Set(),
      outputs = Set(),
      attributes = Set()
    )

    val toNodes1 = ToNodes(clazz = source1, nodeNames = Set(""))

    val edge1 = Edge("", mReference, Set(toNodes1), Set(), Map.empty)

    rule.isValid(edge1).get should be(true)

    val source2 = MClass(
      name = "source2",
      abstractness = false,
      superTypeNames = Set(),
      inputs = Set(),
      outputs = Set(),
      attributes = Set()
    )

    val toNodes2 = ToNodes(clazz = source1, nodeNames = Set("", ""))

    val edge2 = Edge("", mReference, Set(toNodes2), Set(), Map.empty)

    rule.isValid(edge2).get should be(true)

  }

  it should "return false on edges of type edgeType with invalid source nodes" in {
    val invalidSource = MClass(
      name = "invalidSource",
      abstractness = false,
      superTypeNames = Set(),
      inputs = Set(),
      outputs = Set(),
      attributes = Set()
    )

    val invalidToNodes = ToNodes(clazz = invalidSource, nodeNames = Set(""))

    val edge1 = Edge("", mReference, Set(invalidToNodes), Set(), Map.empty)

    rule.isValid(edge1).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentMReference = MReference(
      "differentEdgeType",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Set.empty,
      Set.empty,
      Set[MAttribute]()
    )
    val edge = Edge("", differentMReference, Set(), Set(), Map.empty)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Sources ofEdges "edgeType" areOfTypes Set("source1", "source2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val class1 = MClass("class1", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val class2 = MClass("class2", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val sourceLinkDef1 = MClassLinkDef(class1.name, -1, 0, deleteIfLower = false)
    val sourceLinkDef2 = MClassLinkDef(class2.name, -1, 0, deleteIfLower = false)
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set(sourceLinkDef1, sourceLinkDef2),
      Set.empty, Set[MAttribute]())
    val metaModel = TestUtil.referencesToMetaModel(Set(reference))
    val result = EdgeSourceNodes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeSourceNodes =>
        rule.edgeType should be("reference")
        rule.sourceTypes should be(Set("class1", "class2"))
      case _ => fail
    }
  }

}
