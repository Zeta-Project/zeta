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

class EdgeSourcesLowerBoundTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType",
    description = "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute](),
    Seq.empty
  )
  val emptyEdge: Edge = Edge.empty("", mReference.name, Seq.empty, Seq.empty)
  val rule = new EdgeSourcesLowerBound("edgeType", "sourceType", 2)

  "isValid" should "return true on edges of type edgeType having 2 or more source nodes of type sourceType" in {
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

    val threeSourceNodes = NodeLink(className = sourceType.name, nodeNames = Seq("", "", ""))

    val edgeThreeSourceNodes = emptyEdge.copy(source = Seq(threeSourceNodes))

    rule.isValid(edgeThreeSourceNodes).get should be(true)
  }

  it should "return false on edges of type edgeType having less than 2 source nodes of type sourceType" in {
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

    val oneSourceNode = NodeLink(className = sourceType.name, nodeNames = Seq(""))

    val edgeOneSourceNode = emptyEdge.copy(source = Seq(oneSourceNode))

    rule.isValid(edgeOneSourceNode).get should be(false)

    val edgeNoSourceNodes = emptyEdge

    rule.isValid(edgeNoSourceNodes).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentMRef = MReference(
      "invalidReference",
      description = "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      Seq.empty,
      Seq.empty,
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = emptyEdge.copy(referenceName = differentMRef.name)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Sources ofEdges "edgeType" toNodes "sourceType" haveLowerBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val class1 = MClass("class", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val sourceLinkDef1 = MClassLinkDef(class1.name, -1, 5, deleteIfLower = false)
    val reference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(sourceLinkDef1), Seq.empty,
      Seq[MAttribute](), Seq.empty)
    val metaModel = TestUtil.referencesToMetaModel(Seq(reference))
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
