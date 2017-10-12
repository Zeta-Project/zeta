package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.NodeLink
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeTargetNodesTest extends FlatSpec with Matchers {
  val mReference = MReference(
    "edgeType",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty, Seq.empty,
    Seq[MAttribute](),
    Seq.empty
  )
  val emptyEdge: Edge = Edge.empty("", mReference.name, Seq.empty, Seq.empty)
  val rule = new EdgeTargetNodes("edgeType", Seq("target1", "target2"))

  "isValid" should "return true on edges of type edgeType with valid target nodes" in {

    val target1 = MClass(
      name = "target1",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val toNodes1 = NodeLink(className = target1.name, nodeNames = Seq(""))

    val edge1 = emptyEdge.copy(target = Seq(toNodes1))

    rule.isValid(edge1).get should be(true)

    val target2 = MClass(
      name = "target2",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val toNodes2 = NodeLink(className = target1.name, nodeNames = Seq("", ""))

    val edge2 = emptyEdge.copy(target = Seq(toNodes2))

    rule.isValid(edge2).get should be(true)

  }

  it should "return false on edges of type edgeType with invalid target nodes" in {
    val invalidTarget = MClass(
      name = "invalidTarget",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val invalidToNodes = NodeLink(className = invalidTarget.name, nodeNames = Seq(""))

    val edge1 = emptyEdge.copy(target = Seq(invalidToNodes))

    rule.isValid(edge1).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentMReference = MReference(
      "differentEdgeType",
      description = "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = emptyEdge.copy(referenceName = differentMReference.name)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Targets ofEdges "edgeType" areOfTypes Seq("target1", "target2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val class1 = MClass("class1", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val class2 = MClass("class2", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val targetLinkDef1 = MClassLinkDef(class1.name, -1, 0, deleteIfLower = false)
    val targetLinkDef2 = MClassLinkDef(class2.name, -1, 0, deleteIfLower = false)
    val reference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq.empty, Seq(targetLinkDef1,
      targetLinkDef2), Seq[MAttribute](), Seq.empty)
    val metaModel = TestUtil.referencesToMetaModel(Seq(reference))
    val result = EdgeTargetNodes.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgeTargetNodes =>
        rule.edgeType should be ("reference")
        rule.targetTypes should be (Seq("class1", "class2"))
      case _ => fail
    }
  }
}
