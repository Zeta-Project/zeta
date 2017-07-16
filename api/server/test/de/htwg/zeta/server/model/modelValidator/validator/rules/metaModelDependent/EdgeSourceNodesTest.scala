package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

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
    description = "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    Seq.empty,
    Seq.empty,
    Seq[MAttribute](),
    Seq.empty
  )
  val rule = new EdgeSourceNodes("edgeType", Seq("source1", "source2"))

  "isValid" should "return true on edges of type edgeType with valid source nodes" in {

    val source1 = MClass(
      name = "source1",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val toNodes1 = ToNodes(clazz = source1, nodeNames = Seq(""))

    val edge1 = Edge("", mReference, Seq(toNodes1), Seq(), Map.empty)

    rule.isValid(edge1).get should be(true)

    val source2 = MClass(
      name = "source2",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val toNodes2 = ToNodes(clazz = source1, nodeNames = Seq("", ""))

    val edge2 = Edge("", mReference, Seq(toNodes2), Seq(), Map.empty)

    rule.isValid(edge2).get should be(true)

  }

  it should "return false on edges of type edgeType with invalid source nodes" in {
    val invalidSource = MClass(
      name = "invalidSource",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val invalidToNodes = ToNodes(clazz = invalidSource, nodeNames = Seq(""))

    val edge1 = Edge("", mReference, Seq(invalidToNodes), Seq(), Map.empty)

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
    val edge = Edge("", differentMReference, Seq(), Seq(), Map.empty)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Sources ofEdges "edgeType" areOfTypes Seq("source1", "source2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val class1 = MClass("class1", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val class2 = MClass("class2", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val sourceLinkDef1 = MClassLinkDef(class1.name, -1, 0, deleteIfLower = false)
    val sourceLinkDef2 = MClassLinkDef(class2.name, -1, 0, deleteIfLower = false)
    val reference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq(sourceLinkDef1, sourceLinkDef2),
      Seq.empty, Seq[MAttribute](), Seq.empty)
    val metaModel = TestUtil.referencesToMetaModel(Seq(reference))
    val result = EdgeSourceNodes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeSourceNodes =>
        rule.edgeType should be("reference")
        rule.sourceTypes should be(Seq("class1", "class2"))
      case _ => fail
    }
  }

}
