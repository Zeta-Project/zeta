package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeTargetNodesTest extends FlatSpec with Matchers {
  val mReference = MReference(
    "edgeType",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Set.empty, Set.empty,
    Set[MAttribute]()
  )
  val rule = new EdgeTargetNodes("edgeType", Seq("target1", "target2"))

  "isValid" should "return true on edges of type edgeType with valid target nodes" in {

    val target1 = MClass(
      name = "target1",
      abstractness = false,
      superTypeNames = Set(),
      inputs = Set(),
      outputs = Set(),
      attributes = Set()
    )

    val toNodes1 = ToNodes(clazz = target1, nodeNames = Set(""))

    val edge1 = Edge("", mReference, Set(), Set(toNodes1), Map.empty)

    rule.isValid(edge1).get should be(true)

    val target2 = MClass(
      name = "target2",
      abstractness = false,
      superTypeNames = Set(),
      inputs = Set(),
      outputs = Set(),
      attributes = Set()
    )

    val toNodes2 = ToNodes(clazz = target1, nodeNames = Set("", ""))

    val edge2 = Edge("", mReference, Set(), Set(toNodes2), Map.empty)

    rule.isValid(edge2).get should be(true)

  }

  it should "return false on edges of type edgeType with invalid target nodes" in {
    val invalidTarget = MClass(
      name = "invalidTarget",
      abstractness = false,
      superTypeNames = Set(),
      inputs = Set(),
      outputs = Set(),
      attributes = Set()
    )

    val invalidToNodes = ToNodes(clazz = invalidTarget, nodeNames = Set(""))

    val edge1 = Edge("", mReference, Set(), Set(invalidToNodes), Map.empty)

    rule.isValid(edge1).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentMReference = MReference(
      "differentEdgeType",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Set.empty,
      Set.empty,
      Set[MAttribute]())
    val edge = Edge("", differentMReference, Set(), Set(), Map.empty)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Targets ofEdges "edgeType" areOfTypes Set("target1", "target2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val class1 = MClass("class1", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val class2 = MClass("class2", abstractness = false, Set.empty, Set.empty, Set.empty, Set[MAttribute]())
    val targetLinkDef1 = MClassLinkDef(class1.name, -1, 0, deleteIfLower = false)
    val targetLinkDef2 = MClassLinkDef(class2.name, -1, 0, deleteIfLower = false)
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Set.empty, Set(targetLinkDef1,
      targetLinkDef2), Set[MAttribute]())
    val metaModel = TestUtil.referencesToMetaModel(Set(reference))
    val result = EdgeTargetNodes.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgeTargetNodes =>
        rule.edgeType should be ("reference")
        rule.targetTypes should be (Set("class1", "class2"))
      case _ => fail
    }
  }
}
