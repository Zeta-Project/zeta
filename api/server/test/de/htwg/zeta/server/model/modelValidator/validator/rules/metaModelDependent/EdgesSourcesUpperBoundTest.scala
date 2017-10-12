package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClassLinkDef
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.NodeLink
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesSourcesUpperBoundTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute](),
    Seq.empty
  )
  val emptyEdge: Edge = Edge.empty("", mReference.name, Seq.empty, Seq.empty)
  val rule = new EdgeSourcesUpperBound("edgeType", "sourceType", 2)

  "isValid" should "return true on edges of type edgeType having 2 or less source nodes of type sourceType" in {
    val sourceType = MClass(
      name = "sourceType",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val twoSourceNodes = NodeLink(className = sourceType.name, nodeNames = Seq("", ""))

    val edgeTwoSourceNodes = emptyEdge.copy(source = Seq(twoSourceNodes))

    rule.isValid(edgeTwoSourceNodes).get should be(true)


    val oneSourceNode = NodeLink(className = sourceType.name, nodeNames = Seq(""))

    val edgeOneSourceNode = Edge(UUID.randomUUID(), mReference.name, Seq(oneSourceNode), Seq(), Map.empty)

    rule.isValid(edgeOneSourceNode).get should be(true)


    val edgeNoSourceNodes = Edge(UUID.randomUUID(), mReference.name, Seq(), Seq(), Map.empty)

    rule.isValid(edgeNoSourceNodes).get should be(true)
  }

  it should "return false on edges of type edgeType having more than 2 source nodes of type sourceType" in {
    val sourceType = MClass(
      name = "sourceType",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val threeSourceNodes = NodeLink(className = sourceType.name, nodeNames = Seq(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()))

    val edgeThreeSourceNodes = Edge(UUID.randomUUID(), mReference.name, Seq(threeSourceNodes), Seq(), Map.empty)

    rule.isValid(edgeThreeSourceNodes).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference(
      "differentEdgeType",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = Edge(UUID.randomUUID(), differentReference.name, Seq(), Seq(), Map.empty)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Sources ofEdges "edgeType" toNodes "sourceType" haveUpperBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val class1 = MClass("class", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val sourceLinkDef1 = MClassLinkDef(class1.name, 7, 0, deleteIfLower = false)
    val reference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(sourceLinkDef1), Seq.empty,
      Seq.empty, Seq.empty)
    val metaModel = TestUtil.referencesToMetaModel(Seq(reference))
    val result = EdgeSourcesUpperBound.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgeSourcesUpperBound =>
        rule.edgeType should be ("reference")
        rule.sourceType should be ("class")
        rule.upperBound should be (7)
      case _ => fail
    }
  }

}
